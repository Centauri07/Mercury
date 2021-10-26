package me.centauri07.mercury.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import me.centauri07.mercury.Mercury;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@CommandInformation(name = "help", description = "lists of commands", usage = "help")
public class HelpCommand extends TextCommandHandler {
    @Override
    public void perform(@NotNull TextCommandEvent textCommandEvent) {
        List<TextCommandHandler> commandHandlers =
                Mercury.getCommandManager().getCommands().values().stream().filter(command -> textCommandEvent.getMember().hasPermission(command.getPermissions())).collect(Collectors.toList());

        if (!commandHandlers.isEmpty()) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Commands");
            embedBuilder.setColor(Color.CYAN);
            embedBuilder.setTimestamp(new Date().toInstant());
            commandHandlers.forEach( commandHandler -> {
                        embedBuilder.addField(
                                commandHandler.getCommandInfo().name() + " | " + commandHandler.getCommandInfo().description(),
                                "$" +commandHandler.getCommandInfo().usage(),
                                false
                                );
                    }
            );

            textCommandEvent.getMessage().replyEmbeds(embedBuilder.build()).mentionRepliedUser(false).queue();
        }
    }
}
