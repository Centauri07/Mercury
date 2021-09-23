package me.centauri07.mercury.school.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.attributes.RequiredArgs;
import me.centauri07.command.attributes.RequiredArgsRange;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.event.TextSubCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import me.centauri07.command.text.subcommand.TextSubCommand;
import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.school.section.Section;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.stream.Collectors;

@RequiredArgsRange(lowerBound = 0, upperBound = 2)
@CommandInformation(name = "section", description = "Grade sections", usage = "section <create | remove> <name>")
public class SectionCommand extends TextCommandHandler {
    @Override
    public void perform(@NotNull TextCommandEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        Section section = Mercury.getSectionManager().getSectionByMemberInGuild(event.getGuild(), event.getMember());
        if (section == null) {
            event.reply("You're not in a section", Color.RED);
            return;
        }
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(section.getName());
        embedBuilder.addField("Students", String.valueOf(section.getStudents().size()), false);
        embedBuilder.addField("Subjects", String.valueOf(section.getSubjects().size()), false);

        int tasksCount = 0;
        for (int i : section.getSubjects().stream().map(subject -> subject.getTasks().size()).collect(Collectors.toList())) {
            tasksCount += i;
        }
        embedBuilder.addField("Tasks", String.valueOf(tasksCount), false);

        event.getMessage().replyEmbeds(embedBuilder.build()).mentionRepliedUser(false).queue();
    }

    @RequiredArgs(requiredArguments = 1)
    @TextSubCommand(name = "create")
    public void create(TextSubCommandEvent event) {
        if (Mercury.getSectionManager().getSection(event.getGuild() , event.getArgs()[0]) != null) {
            event.reply(String.format("Section %s already exists", event.getArgs()[0]), Color.RED);
            return;
        }

        event.getMessage().replyEmbeds(new EmbedBuilder().setDescription("Creating section...").setColor(Color.CYAN).build()).mentionRepliedUser(false).queue(message -> {
                    Mercury.getSectionManager().createSection(event.getGuild(), event.getArgs()[0]);
                    message.editMessageEmbeds(new EmbedBuilder().setDescription("Section successfully created!").setColor(Color.GREEN).build()).mentionRepliedUser(false).queue();
                }
        );
    }

    @RequiredArgs(requiredArguments = 1)
    @TextSubCommand(name = "remove")
    public void remove(TextSubCommandEvent event) {
        if (Mercury.getSectionManager().getSection(event.getGuild() , event.getArgs()[0]) == null) {
            event.reply("Section does not exists!", Color.RED);
            return;
        }

        event.getMessage().replyEmbeds(new EmbedBuilder().setColor(Color.CYAN).setDescription("Deleting section...").build()).mentionRepliedUser(false).queue(
                message -> {
                    Mercury.getSectionManager().removeSection(event.getGuild(), event.getArgs()[0]);
                    message.editMessageEmbeds(new EmbedBuilder().setColor(Color.GREEN).setDescription("Section successfully deleted!").build()).queue();
                }
        );
    }
}