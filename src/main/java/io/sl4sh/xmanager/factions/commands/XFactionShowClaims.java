package io.sl4sh.xmanager.factions.commands;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import io.sl4sh.xmanager.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.factions.XFaction;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleOptions;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.FireworkEffect;
import org.spongepowered.api.item.FireworkShapes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Color;

import java.util.ArrayList;
import java.util.List;

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

        XFaction CallerFaction = XFactionCommandManager.getPlayerFaction(Caller);

        if(CallerFaction == null) { Caller.sendMessage(Text.of(XError.XERROR_NOXF.getDesc())); return; }

        for(String ChunkStr : CallerFaction.getFactionClaims()){

            Vector3i ChunkLoc = XManager.getStringAsVector3i(ChunkStr);

            Vector3d ChunkMax = Caller.getWorld().getChunk(ChunkLoc).get().getBlockMax().toDouble();
            Vector3d ChunkMin = Caller.getWorld().getChunk(ChunkLoc).get().getBlockMin().toDouble();

            Vector3d SpawnLoc = new Vector3d((ChunkMax.getX() + ChunkMin.getX()) / 2, 80.0, (ChunkMax.getZ() + ChunkMin.getZ()) / 2);

            List<FireworkEffect> FwEffects =  new ArrayList<FireworkEffect>();
            FwEffects.add(FireworkEffect.builder().shape(FireworkShapes.STAR).colors(Color.LIME).fade(Color.WHITE).trail(true).flicker(true).build());

            ParticleEffect effect = ParticleEffect.builder().type(ParticleTypes.FIREWORKS).quantity(1000).option(ParticleOptions.FIREWORK_EFFECTS, FwEffects).build();

            Caller.spawnParticles(effect, SpawnLoc, 250);

        }

    }

}
