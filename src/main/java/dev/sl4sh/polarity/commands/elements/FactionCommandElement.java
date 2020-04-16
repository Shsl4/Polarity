package dev.sl4sh.polarity.commands.elements;

import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FactionCommandElement extends CommandElement {

    public FactionCommandElement(@Nullable Text key) {
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
        List<String> existingNames = Utilities.getExistingFactionsNames();

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
