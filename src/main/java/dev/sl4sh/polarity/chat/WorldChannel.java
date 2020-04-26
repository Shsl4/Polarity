package dev.sl4sh.polarity.chat;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class WorldChannel extends PolarityMutableChannel {

    public WorldChannel(List<MessageReceiver> members) {
        super(members);
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {

        if(sender instanceof Player && getMembers().contains(sender)){

            Player ply = (Player)sender;
            String message = original.toPlain().replace("&", "\u00a7").replace(ply.getName(),"");
            Optional<Faction> optionalFaction = Utilities.getPlayerFaction(ply);

            if(optionalFaction.isPresent()){

                if(!optionalFaction.get().getPrefix().isEmpty()){

                    return Optional.of(Text.of(optionalFaction.get().getPrefix(), TextColors.RESET, " <", ply.getName(), "> ", message));

                }
                else{

                    return Optional.of(Text.of("[Faction]", TextColors.RESET, " <", ply.getName(), "> ", message));

                }

            }
            else{

                return Optional.of(Text.of(TextColors.GREEN, "[Adventurer]", TextColors.RESET, " <", ply.getName(), "> ", message));

            }

        }

        return Optional.empty();
        
    }
}
