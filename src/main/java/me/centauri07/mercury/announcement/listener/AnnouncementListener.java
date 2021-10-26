package me.centauri07.mercury.announcement.listener;

import me.centauri07.mercury.announcement.AnnouncementSession;
import me.centauri07.mercury.announcement.Field;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class AnnouncementListener extends ListenerAdapter {

    private final Button desc = Button.primary("description", "\uD83D\uDCF0 Description");
    private final Button fields = Button.primary("fields", "\uD83D\uDCD2 Fields");
    private final Button descAndFields = Button.primary("desc-fields", "\uD83D\uDCDA Description and Fields");

    private final Button confirm = Button.success("confirm", "✅ Confirm");
    private final Button cancel = Button.danger("cancel", "❌ Cancel");

    private final Button addField = Button.primary("add-field", "➕ Add Field");

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        AnnouncementSession announcementSession = AnnouncementSession.getCache(event.getAuthor().getId());

        if (announcementSession != null) {
            announcementSession.resetTimer();

            if (announcementSession.getColor() == null) {
                Color color = me.centauri07.mercury.util.Color.getColor(event.getMessage().getContentRaw());

                if (color == null) {
                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> {
                                privateChannel.sendMessageEmbeds(
                                        new EmbedBuilder().setDescription("Color does not exist").setColor(Color.RED)
                                                .build()).queue();
                            }
                    );
                }

                announcementSession.setColor(color);

                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> {
                            privateChannel.sendMessageEmbeds(announcementSession.getEmbedBuilder().build()).queue();
                            privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder()
                                            .setDescription("Enter Announcement Title")
                                            .setColor(Color.CYAN).build()
                            ).queue();
                        }
                );

            } else if (announcementSession.getTitle() == null) {
                announcementSession.setTitle(event.getMessage().getContentRaw());
                event.getAuthor().openPrivateChannel().queue(
                        privateChannel -> {
                            privateChannel.sendMessageEmbeds(announcementSession.getEmbedBuilder().build()).queue();
                            privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder()
                                            .setDescription("Select content for your Announcement (\uD83D\uDCF0 for description, \uD83D\uDCD2 for Fields, and \uD83D\uDCDA for both)")
                                            .setColor(Color.CYAN).build()
                            ).setActionRow(desc, fields, descAndFields).queue();
                        }
                );

            } else if (announcementSession.isDescription()) {
                System.out.println("desc");
                if (announcementSession.getDescription() == null) {
                    announcementSession.setIsDescription(false);
                    announcementSession.setDescription(event.getMessage().getContentRaw());
                    event.getAuthor().openPrivateChannel().queue(
                            privateChannel -> {
                                privateChannel.sendMessageEmbeds(announcementSession.getEmbedBuilder().build()).queue();

                                if (announcementSession.isField()) {
                                    System.out.println("yes");
                                    privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.CYAN)
                                            .setDescription("Enter Field Title").build()).queue();
                                } else {
                                    privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.CYAN)
                                            .setDescription("React with ✅ to announce it to the server and react with ❌ to cancel")
                                            .build()).setActionRow(confirm, cancel).queue();
                                }
                            }
                    );

                }
            } else if (announcementSession.isField()) {
                System.out.println("field");
                Field field = AnnouncementSession.getFieldCache(event.getAuthor().getId());
                if (field != null) {
                    if (!field.hasTitle()) {
                        field.setTitle(event.getMessage().getContentRaw());
                        event.getAuthor().openPrivateChannel().queue(
                                privateChannel -> privateChannel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.CYAN)
                                        .setDescription("Enter Field Description").build()).queue()
                        );
                    } else if (!field.hasDescription()) {
                        announcementSession.setIsDescription(false);
                        field.setDescription(event.getMessage().getContentRaw());
                        announcementSession.addField(field);

                        AnnouncementSession.removeFieldCache(event.getAuthor().getId());

                        event.getAuthor().openPrivateChannel().queue(
                                privateChannel -> {
                                    privateChannel.sendMessageEmbeds(announcementSession.getEmbedBuilder().build()).queue();

                                    privateChannel.sendMessageEmbeds(
                                            new EmbedBuilder().setColor(Color.CYAN)
                                                    .setTitle("React with ➕ to add field, react with ✅ to announce it to the server and react with ❌ to cancel")
                                                    .build()
                                    ).setActionRow(addField, confirm, cancel).queue();
                                }
                        );
                    }
                }
            }
        }
    }

    @Override
    public void onButtonClick(@NotNull ButtonClickEvent event) {
        event.deferEdit().queue();

        AnnouncementSession announcementSession = AnnouncementSession.getCache(event.getUser().getId());

        if (announcementSession != null) {
            event.getMessage().delete().queue();

            announcementSession.resetTimer();

            Button button = event.getButton();
            if (button != null) {
                if (button.equals(desc)) {
                    announcementSession.setIsDescription(true);

                    event.getUser().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder().setColor(Color.CYAN).setDescription("Enter Announcement Description").build()
                            ).queue()
                    );

                } else if (button.equals(fields)) {
                    announcementSession.setIsField(true);
                    AnnouncementSession.addFieldCache(event.getUser().getId());

                    event.getUser().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder().setColor(Color.CYAN).setDescription("Enter Announcement Field Title").build()
                            ).queue()
                    );

                } else if (button.equals(descAndFields)) {
                    announcementSession.setIsDescription(true);
                    announcementSession.setIsField(true);
                    AnnouncementSession.addFieldCache(event.getUser().getId());

                    event.getUser().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder().setColor(Color.CYAN).setDescription("Enter Announcement Description").build()
                            ).queue()
                    );

                } else if (button.equals(confirm)) {
                    AnnouncementSession.removeCache(event.getUser().getId());

                    announcementSession.getChannel().sendMessage(new MessageBuilder(announcementSession.getEmbedBuilder().build())
                            .setContent(announcementSession.getRole().getAsMention()).build()).queue();

                    event.getUser().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessage(
                                    new EmbedBuilder().setColor(Color.CYAN).setDescription("Announcement has been successfully announced").build()
                            ).queue()
                    );

                } else if (button.equals(cancel)) {
                    AnnouncementSession.removeCache(event.getUser().getId());

                    event.getUser().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder().setColor(Color.CYAN).setDescription("Announcement has been successfully canceled").build()
                            ).queue()
                    );

                } else if (button.equals(addField)) {
                    announcementSession.setIsField(true);
                    AnnouncementSession.addFieldCache(event.getUser().getId());

                    event.getUser().openPrivateChannel().queue(
                            privateChannel -> privateChannel.sendMessageEmbeds(
                                    new EmbedBuilder().setColor(Color.CYAN).setDescription("Enter Announcement Field Title").build()
                            ).queue()
                    );
                }
            }
        }
    }
}
