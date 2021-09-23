package me.centauri07.mercury.listener;

import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.school.session.signup.SignupSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GuildStudentJoinListener extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(@NotNull GuildMemberJoinEvent event) {
        if (event.getUser().isBot()) return;

        if (Mercury.getSectionManager().getSectionsByGuild(event.getGuild()).isEmpty()) return;

        if (Mercury.getSectionManager().getSectionByMemberInGuild(event.getGuild(), event.getMember()) == null) {
            SignupSession.addCache(event.getMember());
            List<net.dv8tion.jda.api.interactions.components.Button> sectionButtons = new ArrayList<>();
            Mercury.getSectionManager().getSectionsByGuild(event.getGuild()).forEach(
                    section -> sectionButtons.add(Button.primary(section.getName().toLowerCase() + "_section", section.getName()))
            );

            event.getUser().openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessageEmbeds(
                            new EmbedBuilder().setColor(Color.CYAN).setDescription("Choose Your Section").build()
                    ).setActionRow(sectionButtons).mentionRepliedUser(false).queue()
            );
        }
    }
}
