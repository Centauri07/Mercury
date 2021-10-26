package me.centauri07.mercury.session;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class SessionListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        Session session = Session.getSession(event.getMember());
        if (session == null) return;
        if (session.channel.equals(event.getChannel())) session.fireEvent(event.getMessage());
    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        Session session = Session.getSession(event.getAuthor());
        if (session == null) return;
        if (session.channel.equals(event.getChannel())) session.fireEvent(event.getMessage());
    }
}
