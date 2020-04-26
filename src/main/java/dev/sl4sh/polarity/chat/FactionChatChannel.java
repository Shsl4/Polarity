package dev.sl4sh.polarity.chat;

import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class FactionChatChannel extends PolarityMutableChannel {

    public FactionChatChannel(List<MessageReceiver> members) {
        super(members);
    }

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {

        if(sender instanceof Player){

            Player player = (Player)sender;

            if(!getMembers().contains(sender)) { return Optional.empty(); }

            String message = original.toPlain().replace("&", "\u00a7").replace(player.getName(),"");

            if(Utilities.getPlayerFaction(player).isPresent() && Utilities.getPlayerFaction((Player)sender).get().getOwner().equals(((Player) sender).getUniqueId())){

                return Optional.of(Text.of(TextColors.RED, "(Faction) ", TextColors.GOLD, "(Owner)", TextColors.RESET, " <" , ((Player)sender).getName() , "> " , message));

            }

            return Optional.of(Text.of(TextColors.RED, "(Faction) ", TextColors.RESET, "<" , ((Player)sender).getName() , "> " , message));

        }

        return Optional.empty();

    }

}
