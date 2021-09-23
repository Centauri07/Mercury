package me.centauri07.mercury.school.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.attributes.RequiredArgsRange;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.school.entity.Student;
import me.centauri07.mercury.school.section.Section;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@RequiredArgsRange(lowerBound = 0, upperBound = 1)
@CommandInformation(name = "student", description = "see student's information", usage = "student [user as mention | user id]")
public class StudentCommand extends TextCommandHandler {
    @Override
    public void perform(@NotNull TextCommandEvent event) {
        Student student;

        Member member;

        if (event.getArgs().length <= 0) {
            member = event.getMember();
        } else {
            if (!event.getMessage().getMentionedMembers().isEmpty()) {
                member = event.getMessage().getMentionedMembers().get(0);
            } else if (event.getGuild().getMemberById(event.getArgs()[0]) != null) {
                member = event.getGuild().getMemberById(event.getArgs()[0]);
            } else {
                event.reply("Member does not exist", Color.RED);
                return;
            }
        }

        Section section = Mercury.getSectionManager().getSectionByMemberInGuild(event.getGuild(), member);

        if (member != null) {
            if (section != null) {
                student = section.getStudent(member);

                if (student != null) {
                    EmbedBuilder embedBuilder = new EmbedBuilder();

                    embedBuilder.setColor(Color.ORANGE);
                    embedBuilder.setAuthor(student.getName(), null, member.getUser().getAvatarUrl());
                    embedBuilder.setTitle(student.getName() + "'s Profile");
                    embedBuilder.addField("Name", student.getName(), false);
                    embedBuilder.addField("Email", student.getEmail(), false);
                    embedBuilder.addField("Birth Date:", String.valueOf(student.getBirthDate()), false);

                    event.getMessage().replyEmbeds(embedBuilder.build()).queue();
                }
            } else {
                event.reply("Student does not exists", Color.RED);
            }

        } else {
            event.reply("Member does not exist", Color.RED);
        }
    }
}
