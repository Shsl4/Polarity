package io.sl4sh.xmanager.commands.factions;

import io.sl4sh.xmanager.enums.XError;
import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import io.sl4sh.xmanager.XFaction;
import io.sl4sh.xmanager.data.factions.XFactionMemberData;
import io.sl4sh.xmanager.tablist.XTabListManager;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

public class XFactionsDisband implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Disbands your faction."))
                .permission("xmanager.factions.disband")
                .executor(new XFactionsDisband())
                .build();
    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if (src instanceof Player) {

            Player caller = (Player) src;

            if(!disbandFaction(caller)){

                caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.75);

            }

        }
        else{

            src.sendMessage(XError.XERROR_PLAYERCOMMAND.getDesc());

        }

        return CommandResult.success();

    }

    private boolean disbandFaction(Player caller){

        Optional<XFaction> optPendingDeleteFaction = XUtilities.getPlayerFaction(caller);

        if(optPendingDeleteFaction.isPresent()){

            XFaction pendingDeleteFaction = optPendingDeleteFaction.get();

            if(pendingDeleteFaction.getFactionOwner().equals(caller.getName())){

                for(XFactionMemberData memberData : pendingDeleteFaction.getFactionMembers()){

                    if(Sponge.getServer().getPlayer(memberData.getPlayerName()).isPresent()){

                        Player factionPlayer = Sponge.getServer().getPlayer(memberData.getPlayerName()).get();
                        factionPlayer.sendMessage(Text.of(TextColors.RED, "[Factions] | Your faction has been disbanded by the owner. (", caller.getName(), ")"));
                        factionPlayer.playSound(SoundTypes.AMBIENT_CAVE, factionPlayer.getPosition(), 0.75);

                    }

                }

                XManager.getFactions().remove(pendingDeleteFaction);
                XManager.getXManager().writeFactionsConfigurationFile();
                XTabListManager.refreshTabLists();
                return true;

            }
            else{

                caller.sendMessage(Text.of(TextColors.AQUA, "[Factions] | The faction can only be disbanded by the owner."));

            }

        }
        else{

            caller.sendMessage(XError.XERROR_NOXF.getDesc());

        }

        return false;

    }

}
