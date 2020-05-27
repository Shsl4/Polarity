package dev.sl4sh.polarity.commands.factions;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.FactionCommandElement;
import dev.sl4sh.polarity.data.factions.FactionMemberData;
import dev.sl4sh.polarity.enums.PolarityErrors;
import dev.sl4sh.polarity.enums.PolarityInfo;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.effect.sound.SoundTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public class FactionsAlly implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Factions alliance command. Prints help if no argument is provided."))
                .arguments(new FactionCommandElement(Text.of("factionName")))
                .permission("polarity.factions.ally")
                .executor(new FactionsAlly())
                .build();

    }

    @NonNull
    @Override
    public CommandResult execute(@NonNull CommandSource src, @NonNull CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { throw new CommandException(Text.of(TextColors.RED, "This is a player only command.")); }

        Player caller = (Player)src;

        requestAllyToFaction(caller, (String)args.getOne("factionName").get());

        return CommandResult.success();

    }

    private void requestAllyToFaction(Player caller, String targetFactionName){

        Optional<Faction> optTargetFaction = Utilities.getFactionByName(targetFactionName);

        // Return if the caller has no faction
        if(!optTargetFaction.isPresent()) {  caller.sendMessage(Text.of(PolarityErrors.NULLFACTION.getDesc())); return; }

        Optional<Faction> optCallerFaction = Utilities.getPlayerFaction(caller);

        // Return if the provided faction doesn't exist
        if(!optCallerFaction.isPresent()) {  caller.sendMessage(Text.of(PolarityErrors.NOFACTION.getDesc())); return; }

        if(optCallerFaction.get().getUniqueId() == optTargetFaction.get().getUniqueId()) { caller.sendMessage(Text.of(TextColors.AQUA, "You cannot ally your own faction!")); return;}

        // Return if the faction is already allied
        if(optCallerFaction.get().isFactionAllied(optTargetFaction.get())) { caller.sendMessage(Text.of(PolarityInfo.XERROR_ALREADYALLIED.getDesc())); return;}

        Optional<FactionMemberData> optTargetMemberData = Utilities.getMemberDataForPlayer(caller);

        // Return if the permission data is inaccessible or if the caller is not allowed to configure the faction
        if(!optTargetMemberData.isPresent() || !optTargetMemberData.get().permissions.getManage()) {  caller.sendMessage(Text.of(PolarityErrors.UNAUTHORIZED.getDesc())); return; }

        caller.sendMessage(Text.of(TextColors.GREEN, "Successfully submitted your ally request to " , optTargetFaction.get().getDisplayName()));

        AtomicBoolean validated = new AtomicBoolean(false);

        Text accept = Text.builder().onClick(TextActions.executeCallback((source) -> {

            if(source instanceof Player){

                ((Player)source).playSound(SoundTypes.UI_BUTTON_CLICK, ((Player)source).getPosition(), 0.25);

            }

            if(validated.get()) { source.sendMessage(Text.of(TextColors.AQUA, "This request has already been processed")); return; }

            optCallerFaction.get().getAllies().add(optTargetFaction.get().getUniqueId());
            optTargetFaction.get().getAllies().add(optCallerFaction.get().getUniqueId());
            validated.set(true);

            for(FactionMemberData targetFactionMbData : optTargetFaction.get().getMemberDataList()){

                Optional<Player> targetFactionConfigPlayer = Utilities.getPlayerByUniqueID(targetFactionMbData.getPlayerUniqueID());

                if(targetFactionConfigPlayer.isPresent()){

                    targetFactionConfigPlayer.get().sendMessage(Text.of(TextColors.AQUA, "", optCallerFaction.get().getDisplayName(), TextColors.RESET, TextColors.AQUA, " are now your allies!"));
                    targetFactionConfigPlayer.get().playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, targetFactionConfigPlayer.get().getPosition(), 0.25);

                }

            }

            for(FactionMemberData targetFactionMbData : optCallerFaction.get().getMemberDataList()){

                Optional<Player> callerFactionConfigPlayer = Utilities.getPlayerByUniqueID(targetFactionMbData.getPlayerUniqueID());

                if(callerFactionConfigPlayer.isPresent()){

                    callerFactionConfigPlayer.get().sendMessage(Text.of(TextColors.AQUA, "", optTargetFaction.get().getDisplayName(), TextColors.RESET, TextColors.AQUA, " are now your allies!"));
                    callerFactionConfigPlayer.get().playSound(SoundTypes.BLOCK_NOTE_XYLOPHONE, callerFactionConfigPlayer.get().getPosition(), 0.25);

                }

            }

        })).append(Text.of(TextStyles.UNDERLINE, TextColors.GREEN, "Accept")).build();

        Text decline = Text.builder().onClick(TextActions.executeCallback((source) -> {

            if(source instanceof Player){

                ((Player)source).playSound(SoundTypes.UI_BUTTON_CLICK, ((Player)source).getPosition(), 0.25);

            }

            if(!validated.get()){

                source.sendMessage(Text.of(TextColors.AQUA, "Successfully declined the alliance request."));
                caller.sendMessage(Text.of(TextColors.RED, "", optCallerFaction.get().getDisplayName(), TextColors.RESET, TextColors.RED, " declined your alliance request."));
                caller.playSound(SoundTypes.BLOCK_NOTE_BASS, caller.getPosition(), 0.25);
                validated.set(true);

            }
            else{

                source.sendMessage(Text.of(TextColors.AQUA, "This request has already been processed"));

            }

        })).append(Text.of(TextStyles.UNDERLINE, TextColors.RED, "Decline")).build();

        // For each TARGET faction's member
        for(FactionMemberData playerData : optTargetFaction.get().getMemberDataList()){

            // If the player is allowed to configure the faction
            if(playerData.permissions.getManage()){

                Optional<Player> optTargetFactionConfigPlayer = Utilities.getPlayerByUniqueID(playerData.getPlayerUniqueID());
                optTargetFactionConfigPlayer.ifPresent(player -> player.sendMessage(Text.of(TextColors.AQUA, "", optCallerFaction.get().getDisplayName(), TextColors.RESET, TextColors.AQUA, " just sent you an alliance request. ", accept, TextStyles.RESET, TextColors.GRAY, " | ", decline)));

            }

        }

    }

}
