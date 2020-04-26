package dev.sl4sh.polarity.chat;

import dev.sl4sh.polarity.Faction;
import dev.sl4sh.polarity.Utilities;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

public class GeneralChannel implements MessageChannel {

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {

        if(sender instanceof Player){

            Player player = (Player)sender;

            String message = original.toPlain().replace("&", "\u00a7").replace(player.getName(),"");

            Optional<Faction> optionalFaction = Utilities.getPlayerFaction(player);

            if(optionalFaction.isPresent()) {

                if(optionalFaction.get().getPrefix().isEmpty()){

                    return Optional.of(Text.of(TextColors.GRAY, "(General) ", TextColors.RESET, "[Faction]", " <", player.getName(), "> ", message));

                }
                else{

                    return Optional.of(Text.of(TextColors.GRAY, "(General) ", TextColors.RESET, optionalFaction.get().getPrefix(), TextColors.RESET, " <", player.getName(), "> ", message));

                }


            }
            else {

                return Optional.of(Text.of(TextColors.GRAY, "(General) ", TextColors.RESET, TextColors.GREEN, "[Adventurer]", TextColors.RESET, " <", player.getName(), "> ", message));

            }

        }

        return Optional.of(original);
        
    }

    @Nonnull
    @Override
    public Collection<MessageReceiver> getMembers() {
        return new ArrayList<>(Sponge.getServer().getOnlinePlayers());
    }
}
