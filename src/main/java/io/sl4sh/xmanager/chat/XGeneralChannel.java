package io.sl4sh.xmanager.chat;

import io.sl4sh.xmanager.factions.XFaction;
import io.sl4sh.xmanager.factions.commands.XFactionCommandManager;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.channel.MessageChannel;
import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.format.TextColors;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class XGeneralChannel implements MessageChannel {

    @Override
    public Optional<Text> transformMessage(@Nullable Object sender, MessageReceiver recipient, Text original, ChatType type) {

        if(sender instanceof Player){

            Player ply = (Player)sender;

            Text text = original;

            Optional<XFaction> optFac = XFactionCommandManager.getPlayerFaction(ply);

            if(optFac.isPresent()){

                XFaction fac = optFac.get();

                if(!fac.getFactionPrefix().equals("")){

                    String str = fac.getFactionPrefix().replace("&", "\u00a7");

                    text = Text.of( str + " \u00a7f", TextColors.RESET, text);

                }

            }

            return Optional.of(text);

        }

        return Optional.of(original);

    }

    @Override
    public Collection<MessageReceiver> getMembers() {
        return Collections.emptyList();
    }
}
