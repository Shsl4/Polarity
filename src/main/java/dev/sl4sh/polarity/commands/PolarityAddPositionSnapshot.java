package dev.sl4sh.polarity.commands;

import dev.sl4sh.polarity.Polarity;
import dev.sl4sh.polarity.Utilities;
import dev.sl4sh.polarity.commands.elements.PositionSnapshotTagCommandElement;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.format.TextStyles;
import org.spongepowered.api.world.World;

public class PolarityAddPositionSnapshot implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("Adds a position snapshot for the current world."))
                .permission("polarity.addpositionsnapshot")
                .arguments(new PositionSnapshotTagCommandElement(Text.of("tag")))
                .executor(new PolarityAddPositionSnapshot())
                .build();

    }


    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        if(!(src instanceof Player)) { return CommandResult.success(); }

        Player player = (Player)src;
        World world = player.getWorld();
        String tag = (String)args.getOne("tag").get();
        Utilities.getOrCreateWorldInfo(world).addPositionSnapshot(player.getLocation().getPosition(), player.getRotation(), tag);

        Polarity.getPolarity().writeAllConfig();

        player.sendMessage(Text.of(TextColors.AQUA, "Added tag ", tag, TextStyles.RESET, TextColors.AQUA, " for location ", player.getPosition(), " in world ", world.getName()));

        return CommandResult.success();

    }
}
