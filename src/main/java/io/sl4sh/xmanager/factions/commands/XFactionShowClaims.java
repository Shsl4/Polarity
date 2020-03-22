package io.sl4sh.xmanager.factions.commands;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.inject.Inject;
import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.util.Optional;

public class XFactionShowClaims implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player ply = (Player) src;

            showClaimedChunks(ply);

        }
        else{

            src.sendMessage(Text.of(XError.XERROR_PLAYERCOMMAND.getDesc()));

        }

        return CommandResult.success();

    }

    private void showClaimedChunks(Player Caller){

       Optional<XFaction> optCallerFaction = XFactionCommandManager.getPlayerFaction(Caller);

        if(!optCallerFaction.isPresent()) { Caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        XFaction callerFaction = optCallerFaction.get();

        if(callerFaction.getFactionClaims().size() == 0) { Caller.sendMessage(Text.of(XError.XERROR_NOCLAIMS.getDesc())); return; }

        for(String ChunkStr : callerFaction.getFactionClaims()){

            Vector3i ChunkLoc = XManager.getStringAsVector3i(ChunkStr);

            Vector3d ChunkMax = Caller.getWorld().getChunk(ChunkLoc).get().getBlockMax().toDouble();
            Vector3d ChunkMin = Caller.getWorld().getChunk(ChunkLoc).get().getBlockMin().toDouble();

            //Vector3d ChunkCentre = new Vector3d((ChunkMax.getX() + ChunkMin.getX()) / 2, 80.0, (ChunkMax.getZ() + ChunkMin.getZ()) / 2);

            ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.FLAME).quantity(1).build();

            for(int x = 0; x < 160; x++){

                Vector3d ChunkLim1 = new Vector3d(ChunkMin.getX(), x, ChunkMin.getZ());
                Vector3d ChunkLim2 = new Vector3d(ChunkMax.getX() + 1,  x, ChunkMax.getZ() + 1);
                Vector3d ChunkLim3 = new Vector3d(ChunkMin.getX() + (ChunkMax.getX() - ChunkMin.getX()) + 1,  x, ChunkMin.getZ());
                Vector3d ChunkLim4 = new Vector3d(ChunkMin.getX() ,  x, ChunkMin.getZ() + (ChunkMax.getZ() - ChunkMin.getZ()) + 1);

                Caller.spawnParticles(effect, ChunkLim1);
                Caller.spawnParticles(effect, ChunkLim2);
                Caller.spawnParticles(effect, ChunkLim3);
                Caller.spawnParticles(effect, ChunkLim4);

                XManager.xLogInfo("Lim1: "+ ChunkLim1.toString());
                XManager.xLogInfo("Lim2: "+ ChunkLim2.toString());
                XManager.xLogInfo("Lim3: "+ ChunkLim3.toString());
                XManager.xLogInfo("Lim4: "+ ChunkLim4.toString());


            }

        }

    }

}
