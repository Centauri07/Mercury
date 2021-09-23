package me.centauri07.mercury.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.attributes.Permission;
import me.centauri07.command.attributes.RequiredArgs;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Permission(permissions = net.dv8tion.jda.api.Permission.MESSAGE_MANAGE)
@RequiredArgs(requiredArguments = 1)
@CommandInformation(name = "purge", description = "Deletes messages in a channel", usage = "purge <amount>")
public class PurgeCommand extends TextCommandHandler {
    @Override
    public void perform(@NotNull TextCommandEvent event) {
        int amount;

        try {
            amount = Integer.parseInt(event.getArgs()[0]) + 1;
        } catch (NumberFormatException e) {
            onWrongUsage(event);
            return;
        }

        event.reply("Deleting " + (amount - 1) + " messages", Color.GREEN);

        Executors.newSingleThreadScheduledExecutor().schedule(
                () -> {
                    if (amount > 99) {
                        for (int i = 0; i < (int) Math.floor(amount / 99D); i++) {
                            event.getChannel().purgeMessages(event.getChannel().getHistory().retrievePast(99).complete());
                        }
                    }

                    event.getChannel().purgeMessages(event.getChannel().getHistory().retrievePast(amount % 99).complete());
                }, 3, TimeUnit.SECONDS
        );
    }
}
