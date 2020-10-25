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

public class WarpCommandElement extends CommandElement {

    public WarpCommandElement(@Nullable Text key) {
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
        List<String> existingWarps = Utilities.getExistingWarpNames();

        if(args.hasNext()){

            for(String existingWarp : existingWarps){

                try {
                    if(existingWarp.startsWith(args.peek())){

                        returnList.add(existingWarp);

                    }
                } catch (ArgumentParseException e) {
                    e.printStackTrace();
                }

            }

            return returnList;

        }
        else{

            return existingWarps;

        }

    }

    @Override
    public Text getUsage(CommandSource src) {
        return Text.of("<", getKey(), ">");
    }

}
