package me.centauri07.mercury.announcement.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.attributes.RequiredArgsRange;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import me.centauri07.mercury.session.announcement.AnnouncementSession;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@RequiredArgsRange(lowerBound = 1, upperBound = 2)
@CommandInformation(
        name = "announce",
        description = "announce in your discord server",
        usage = "announce <channel as mention | channel ID> [role id | role as mention]"
)
public class AnnounceCommand extends TextCommandHandler {

    @Override
    public void perform(@NotNull TextCommandEvent event) {
        if (event.getArgs().length <= 1) {
            TextChannel channel;

            if (!event.getMessage().getMentionedChannels().isEmpty()) {
                channel = event.getMessage().getMentionedChannels().get(0);
            } else if (event.getGuild().getTextChannelById(event.getArgs()[0]) != null) {
                channel = event.getGuild().getTextChannelById(event.getArgs()[0]);
            } else {
                event.reply("Channel does not exist", Color.RED);
                return;
            }

            Role role = null;

            if (event.getArgs().length == 2) {
                try {
                    if (!event.getMessage().getMentionedRoles().isEmpty()) {
                        role = event.getMessage().getMentionedRoles().get(0);
                    } else if (event.getGuild().getRoleById(event.getArgs()[0]) != null) {
                        role = event.getGuild().getRoleById(event.getArgs()[0]);
                    } else {
                        event.reply("Role does not exist", Color.RED);
                    }
                } catch (NumberFormatException exception) {
                    event.reply("Role does not exist", Color.RED);
                    return;
                }
            }

            new AnnouncementSession(event.getMember(), event.getAuthor().openPrivateChannel().complete(), channel, role);

            event.getMessage().replyEmbeds(new EmbedBuilder().setColor(Color.CYAN).setDescription("Announcement session started")
                    .build()).queue();
        }
    }
}
