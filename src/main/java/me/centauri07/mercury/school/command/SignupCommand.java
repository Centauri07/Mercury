package me.centauri07.mercury.school.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.school.session.signup.SignupSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@CommandInformation(name = "signup", description = "signup in a section", usage = "signup")
public class SignupCommand extends TextCommandHandler {
    @Override
    public void perform(@NotNull TextCommandEvent event) {
        if (Mercury.getSectionManager().getSectionsByGuild(event.getGuild()).isEmpty()) {
            event.reply("You cannot sign up. There are no sections in your server", Color.RED);
            return;
        }

        if (Mercury.getSectionManager().getSectionByMemberInGuild(event.getGuild(), event.getMember()) == null) {
            SignupSession.addCache(event.getMember());
            List<Button> sectionButtons = new ArrayList<>();
            Mercury.getSectionManager().getSectionsByGuild(event.getGuild()).forEach(
                    section -> sectionButtons.add(Button.primary(section.getName().toLowerCase() + "_section", section.getName()))
            );

            event.getAuthor().openPrivateChannel().queue(
                    privateChannel -> privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder().setColor(Color.CYAN).setDescription("Choose Your Section").build()
                            ).setActionRow(sectionButtons).mentionRepliedUser(false).queue()
            );
        } else {
            event.reply("You're already in a section", Color.RED);
        }
    }
}
