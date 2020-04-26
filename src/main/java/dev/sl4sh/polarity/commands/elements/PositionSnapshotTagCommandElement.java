package dev.sl4sh.polarity.commands.elements;

import dev.sl4sh.polarity.games.PositionSnapshot;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.ArgumentParseException;
import org.spongepowered.api.command.args.CommandArgs;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.args.CommandElement;
import org.spongepowered.api.text.Text;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PositionSnapshotTagCommandElement extends CommandElement {

    public PositionSnapshotTagCommandElement(@Nullable Text key) {
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
        List<String> existingNames = Arrays.asList(PositionSnapshot.Tags.DEFAULT_SPAWN,
                PositionSnapshot.Tags.SPAWN_ANY,
                PositionSnapshot.Tags.TEAM1_SPAWN,
                PositionSnapshot.Tags.TEAM2_SPAWN,
                PositionSnapshot.Tags.TEAM3_SPAWN,
                PositionSnapshot.Tags.TEAM4_SPAWN,
                PositionSnapshot.Tags.BRICK_SPAWN,
                PositionSnapshot.Tags.IRON_SPAWN,
                PositionSnapshot.Tags.GOLD_SPAWN,
                PositionSnapshot.Tags.EMERALD_SPAWN);

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
