package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.PositionSnapshotTagCommandElement;
import dev.sl4sh.polarity.enums.games.ChannelTypes;
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

public class PolarityChannelMain implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Sets the typing channel."))
                .permission("polarity.channel")
                .executor(new PolarityChannelMain())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(src instanceof Player){

            Player player = (Player)src;

            player.sendMessage(Text.of(TextStyles.UNDERLINE, TextColors.GRAY, "Available channels:"));
            player.sendMessage(Text.EMPTY);

            Text clickGeneral = Text.builder().onClick(TextActions.executeCallback((source) -> {
                Utilities.setPreferredChannel(player, ChannelTypes.GENERAL_CHANNEL);
                player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), 0.25);
                player.sendMessage(Text.of(TextColors.GRAY, "Updated your typing channel to ", Utilities.getPreferredChannel(player).getDisplayName()));
            })).append(Text.of(TextStyles.UNDERLINE, TextColors.GOLD, "Switch typing Channel")).build();

            Text clickWorld = Text.builder().onClick(TextActions.executeCallback((source) -> {
                Utilities.setPreferredChannel(player, ChannelTypes.WORLD_CHANNEL);
                player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), 0.25);
                player.sendMessage(Text.of(TextColors.GRAY, "Updated your typing channel to ", Utilities.getPreferredChannel(player).getDisplayName()));
            })).append(Text.of(TextStyles.UNDERLINE, TextColors.GOLD, "Switch typing Channel")).build();

            player.sendMessage(Text.builder().append(Text.of(TextColors.GRAY, "| General channel | : ", TextColors.RESET), clickGeneral).build());

            player.sendMessage(Text.builder().append(Text.of(TextColors.GRAY, "| World channel | : ", TextColors.RESET), clickWorld).build());

            if(Utilities.getPlayerFaction(player).isPresent()){

                Text clickFaction = Text.builder().onClick(TextActions.executeCallback((source) -> {
                    Utilities.setPreferredChannel(player, ChannelTypes.FACTION_CHANNEL);
                    player.playSound(SoundTypes.UI_BUTTON_CLICK, player.getPosition(), 0.25);
                    player.sendMessage(Text.of(TextColors.GRAY, "Updated your typing channel to ", Utilities.getPreferredChannel(player).getDisplayName()));
                })).append(Text.of(TextStyles.UNDERLINE, TextColors.GOLD, "Switch typing Channel")).build();
                player.sendMessage(Text.builder().append(Text.of(TextColors.GRAY, "| Faction channel | : ", TextColors.RESET), clickFaction).build());

            }

            player.sendMessage(Text.EMPTY);
            player.sendMessage(Text.of(TextColors.GRAY, "Your current typing channel is the ", Utilities.getPreferredChannel(player).getDisplayName()));

        }

        return CommandResult.success();

    }
}
