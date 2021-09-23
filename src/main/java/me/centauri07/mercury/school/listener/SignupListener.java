package me.centauri07.mercury.school.listener;

import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.school.section.Section;
import me.centauri07.mercury.school.session.signup.SignupSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class SignupListener extends ListenerAdapter {
    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        SignupSession signupSession = SignupSession.getCache(event.getAuthor().getId());

        if (signupSession != null) {
            if (signupSession.getSection() != null) {
                if (event.getMessage().getContentRaw().equals("$cancel")) {
                    signupSession.stopTimer();

                    SignupSession.removeCache(event.getAuthor().getId());

                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.GREEN)
                                    .setDescription("Successfully canceled").build()).queue()
                    );
                    return;
                }

                signupSession.resetTimer();

                if (signupSession.getStudent().getName() == null) {
                    signupSession.getStudent().setName(event.getMessage().getContentRaw());

                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.CYAN)
                                    .setDescription("Enter Your Email").build()).queue()
                    );

                } else if (signupSession.getStudent().getEmail() == null) {
                    signupSession.getStudent().setEmail(event.getMessage().getContentRaw());

                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.CYAN)
                                    .setDescription("Enter Your Birthdate (Format: Month/Day/Year)\nExample: 01/01/2007").build()).queue()
                    );

                } else if (signupSession.getStudent().getBirthDate() == null) {

                    signupSession.getSection().addStudent(signupSession.getStudent());

                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.GREEN)
                                    .setDescription("You are now a student in " + signupSession.getSection().getName()).build()).queue()
                    );

                    try {
                        signupSession.getStudent().setBirthDate(new SimpleDateFormat("MM/dd/yyyy").parse(event.getMessage().getContentRaw()));
                    } catch (ParseException e) {
                        event.getMessage().replyEmbeds(new EmbedBuilder().setColor(Color.RED).setDescription("Invalid format, please try again")
                                .build()).queue();
                    }
                }
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        SignupSession signupSession = SignupSession.getCache(event.getUser().getId());

        if (signupSession != null) {
            if (signupSession.getSection() == null) {

                if (event.getButton() != null) {
                    Section section = Mercury.getSectionManager().getSection(signupSession.getMember().getGuild(), event.getButton().getLabel());
                    if (section != null) {
                        signupSession.setSection(section);

                        event.getUser().openPrivateChannel().queue(
                                privateChannel -> privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.CYAN)
                                        .setDescription("Enter Your Name").build()).queue()
                        );
                    }
                }
            }
        }
    }
}
