package io.sl4sh.xmanager.commands.economy;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;

public class XEconomyMainCommand implements CommandExecutor {

    public static CommandSpec getCommandSpec(){

        return CommandSpec.builder()
                .description(Text.of("The XEconomy command. Prints help by default."))
                .permission("xmanager.economy")
                .child(XEconomyPlayerTransfer.getCommandSpec(), "playertransfer")
                .child(XEconomyFactionTransfer.getCommandSpec(), "factiontransfer")
                .child(XEconomyAdminTransfer.getCommandSpec(), "admintransfer")
                .child(XEconomyShowBalance.getCommandSpec(), "showbalance")
                .child(XEconomyHelp.getCommandSpec(), "help")
                .executor(new XEconomyMainCommand())
                .build();

    }

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

        XEconomyHelp.printEconomyHelp(src);
        return CommandResult.success();

    }

}
