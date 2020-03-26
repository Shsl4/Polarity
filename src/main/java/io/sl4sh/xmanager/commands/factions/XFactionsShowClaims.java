package io.sl4sh.xmanager.commands.factions;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.data.XManagerLocationData;
import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.enums.XInfo;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionsShowClaims implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Shows your faction's claims."))
                .permission("xmanager.factions.showclaims")
                .executor(new XFactionsShowClaims())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            showClaimedChunks(ply);

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private void showClaimedChunks(Player Caller){

       Optional<XFaction> optCallerFaction = XUtilities.getPlayerFaction(Caller);

        if(!optCallerFaction.isPresent()) { Caller.sendMessage(XError.XERROR_NOXF.getDesc()); return; }

        XFaction callerFaction = optCallerFaction.get();

        if(callerFaction.getFactionClaims().size() == 0) { Caller.sendMessage(XInfo.XERROR_NOCLAIMS.getDesc()); return; }

        for(XManagerLocationData ChunkStr : callerFaction.getFactionClaims()){

            if(ChunkStr.getDimensionName().equals(Caller.getWorld().getName())){

                Vector3i ChunkLoc = XUtilities.getStringAsVector3i(ChunkStr.getLocation());

                if(!Caller.getWorld().getChunk(ChunkLoc).isPresent()) { XError.XERROR_UNKNOWN.getDesc(); return; }

                Vector3d ChunkMax = Caller.getWorld().getChunk(ChunkLoc).get().getBlockMax().toDouble();
                Vector3d ChunkMin = Caller.getWorld().getChunk(ChunkLoc).get().getBlockMin().toDouble();

                //Vector3d ChunkCentre = new Vector3d((ChunkMax.getX() + ChunkMin.getX()) / 2, 80.0, (ChunkMax.getZ() + ChunkMin.getZ()) / 2);

                ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.FLAME).quantity(1).build();

                for(int it = 0; it < 160; it++){

                    Vector3d ChunkLim1 = new Vector3d(ChunkMin.getX(), it, ChunkMin.getZ());
                    Vector3d ChunkLim2 = new Vector3d(ChunkMax.getX() + 1,  it, ChunkMax.getZ() + 1);
                    Vector3d ChunkLim3 = new Vector3d(ChunkMin.getX() + (ChunkMax.getX() - ChunkMin.getX()) + 1,  it, ChunkMin.getZ());
                    Vector3d ChunkLim4 = new Vector3d(ChunkMin.getX() ,  it, ChunkMin.getZ() + (ChunkMax.getZ() - ChunkMin.getZ()) + 1);

                    Caller.spawnParticles(effect, ChunkLim1);
                    Caller.spawnParticles(effect, ChunkLim2);
                    Caller.spawnParticles(effect, ChunkLim3);
                    Caller.spawnParticles(effect, ChunkLim4);

                    /*XManager.xLogInfo("Lim1: "+ ChunkLim1.toString());
                    XManager.xLogInfo("Lim2: "+ ChunkLim2.toString());
                    XManager.xLogInfo("Lim3: "+ ChunkLim3.toString());
                    XManager.xLogInfo("Lim4: "+ ChunkLim4.toString());*/

                }

            }

        }

    }

}
