package me.centauri07.mercury.announcement.command;


import me.centauri07.command.CommandInformation;
import me.centauri07.command.attributes.RequiredArgsRange;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.announcement.AnnouncementSession;
import me.centauri07.mercury.school.entity.Student;
import me.centauri07.mercury.school.section.Section;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@RequiredArgsRange(lowerBound = 0, upperBound = 1)
@CommandInformation(name = "announce", description = "announce in your discord server", usage = "announce <channel as mention | channel ID>")
public class AnnounceCommand extends TextCommandHandler {

    @Override
    public void perform(@NotNull TextCommandEvent event) {
        Section section = Mercury.getSectionManager().getSectionByMemberInGuild(event.getGuild(), event.getMember());

        if (event.getArgs().length <= 0) {
            if (section != null) {
                Student student = section.getStudent(event.getMember());
                if (student != null) {
                    if (student.isOfficer()) {
                        if (event.getArgs().length <= 0) {
                            AnnouncementSession.addCache(event.getAuthor(), section.getRole(), section.getAnnouncementChannel());
                        } else {
                            TextChannel channel;

                            if (!event.getMessage().getMentionedChannels().isEmpty()) {
                                channel = event.getMessage().getMentionedChannels().get(0);
                            } else if (event.getGuild().getTextChannelById(event.getArgs()[0]) != null) {
                                channel = event.getGuild().getTextChannelById(event.getArgs()[0]);
                            } else {
                                event.reply("Channel does not exist", Color.RED);
                                return;
                            }

                            AnnouncementSession.addCache(event.getAuthor(), event.getGuild().getPublicRole(), channel);
                        }

                        event.getAuthor().openPrivateChannel().queue (
                                privateChannel -> privateChannel.sendMessageEmbeds(new EmbedBuilder().setAuthor("Enter Announcement Title")
                                        .setColor(Color.CYAN).build()).queue()
                        );
                    } else {
                        event.reply("You must be an officer to announce", Color.RED);
                    }
                } else {
                    event.reply("You are not a student, please contact a staff / officer to fix this issue", Color.RED);
                }
            } else {
                event.reply("You are not assigned in a section", Color.RED);
            }
        }
    }
}
