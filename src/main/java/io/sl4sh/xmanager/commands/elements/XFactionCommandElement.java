package io.sl4sh.xmanager.commands.elements;

import io.sl4sh.xmanager.XManager;
import io.sl4sh.xmanager.XUtilities;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class XFactionCommandElement extends CommandElement {

    public XFactionCommandElement(@Nullable Text key) {
        super(key);
    }

    @Nullable
    @Override
    protected Object parseValue(CommandSource source, CommandArgs args) throws ArgumentParseException {

        return args.next();

    }

    @Override
    public List<String> complete(CommandSource src, CommandArgs args, CommandContext context) {

        List<String> returnList = new ArrayList<>();
        List<String> existingNames = XUtilities.getExistingFactionsNames();

        for(String existingName : existingNames){

            try {

                if(existingName.startsWith(args.peek())){

                    returnList.add(existingName);

                }

            } catch (ArgumentParseException e) {
                e.printStackTrace();
            }

        }

        return returnList;

    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<", getKey(), ">");
    }

}
