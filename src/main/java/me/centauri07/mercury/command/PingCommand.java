package me.centauri07.mercury.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.attributes.RequiredArgs;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@RequiredArgs(requiredArguments = 0)
@CommandInformation(name = "ping", description = "Shows your ping", usage = "ping")
public class PingCommand extends TextCommandHandler {
    @Override
    public void perform(@NotNull TextCommandEvent event) {
        long start = System.currentTimeMillis();
        event.getMessage().reply(":ping_pong: Pong!").queue(msg -> {
                    long latency = System.currentTimeMillis() - start;

                    Color color;

                    if (latency >= 0 && latency <= 120) {
                        color = Color.GREEN;
                    } else if (latency >= 121 && latency <= 240) {
                        color = Color.YELLOW;
                    } else if (latency >= 241 && latency <= 480) {
                        color = Color.ORANGE;
                    } else {
                        color = Color.RED;
                    }

                    msg.editMessageEmbeds(new EmbedBuilder().setColor(color).setDescription("Your ping is " + latency).build()).queue();
                }
        );
    }
}
