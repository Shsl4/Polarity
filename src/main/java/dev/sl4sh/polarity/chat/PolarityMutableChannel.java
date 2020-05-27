package dev.sl4sh.polarity.chat;

import org.spongepowered.api.text.channel.MessageReceiver;
import org.spongepowered.api.text.channel.MutableMessageChannel;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PolarityMutableChannel implements MutableMessageChannel {

    public PolarityMutableChannel(List<MessageReceiver> members){

        this.members = members;

    }

    @Nonnull
    private List<MessageReceiver> members = new ArrayList<>();

    @Override
    public final boolean addMember(MessageReceiver member) {
         if(members.contains(member)) { return false; } return members.add(member);
    }

    @Override
    public final boolean removeMember(MessageReceiver member) {
        return members.remove(member);
    }

    @Override
    public final void clearMembers() {
        members.clear();
    }

    @Nonnull
    @Override
    public final Collection<MessageReceiver> getMembers() {
        return members;
    }
}
