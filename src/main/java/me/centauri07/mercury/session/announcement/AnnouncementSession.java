package me.centauri07.mercury.session.announcement;

import me.centauri07.mercury.session.Session;
import me.centauri07.mercury.session.Field;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class AnnouncementSession extends Session {
    @Field(
            id = "announcement-color",
            name = "Announcement Color",
            required = true
    )
    public String color;

    @Field(
            id = "announcement-title",
            name = "Announcement Title",
            required = true
    )
    public String title;

    @Field(
            id = "announcement-description",
            name = "Announcement Description",
            required = false
    )
    public String description;

    @Field(
            id = "announcement-field",
            name = "Announcement Field",
            required = false
    )
    public me.centauri07.mercury.session.announcement.Field field = new me.centauri07.mercury.session.announcement.Field();

    /*
    @Field(
            id = "announcement-fields",
            name = "Announcement Fields",
            required = false
    )
    public List<me.centauri07.mercury.session.announcement.Field> fields;
     */

    @Field(
            id = "announcement-image_url",
            name = "Image URL",
            required = false
    )
    public String imageURL;

    public final MessageChannel announcementChannel;

    public final Role role;

    public AnnouncementSession(Member member, MessageChannel announcementChannel, MessageChannel sessionChannel) {
        super(sessionChannel, member);
        this.announcementChannel = announcementChannel;
        this.role = null;

        for (java.lang.reflect.Field field : Arrays.stream(getClass().getDeclaredFields()).filter(it -> it.isAnnotationPresent(Field.class)).collect(Collectors.toList())) {
            field.setAccessible(true);
            try {
                System.out.println(field.getName() + " : " + field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public AnnouncementSession(Member member, MessageChannel sessionChannel, MessageChannel announcementChannel, Role role) {
        super(sessionChannel, member);
        this.announcementChannel = announcementChannel;
        this.role = role;
        System.out.println((int) Arrays.stream(getClass().getDeclaredFields()).filter(it -> it.isAnnotationPresent(Field.class)).count());
        for (java.lang.reflect.Field field : Arrays.stream(getClass().getDeclaredFields()).filter(it -> it.isAnnotationPresent(Field.class)).collect(Collectors.toList())) {
            field.setAccessible(true);
            try {
                System.out.println(field.getName() + " : " + field.get(this));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private Color getColor(String name) {
        java.lang.reflect.Field[] declaredFields = Color.class.getDeclaredFields();
        for (java.lang.reflect.Field field : declaredFields) {
            if (field.getName().equalsIgnoreCase(name)) {
                try {
                    if (field.get(field.getClass()) instanceof Color) {
                        return (Color) field.get(field.getClass());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    public void onSessionFinish() {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        if (getColor(color) != null) embedBuilder.setColor(getColor(color));

        embedBuilder.setTitle(title);

        if (description != null) embedBuilder.setDescription(description);

        /*
        if (fields != null)
            for (me.centauri07.mercury.session.announcement.Field field : fields)
                embedBuilder.addField(field.getTitle(), field.getDescription(), false);

         */

        if (field != null) embedBuilder.addField(field.title, field.description, false);

        if (imageURL != null) embedBuilder.setImage(imageURL);

        announcementChannel.sendMessage(new MessageBuilder().setContent(role != null ? role.getAsMention() : null).setEmbeds(embedBuilder.build()).build()).queue();
    }

    @Override
    public void onSessionExpire() {

    }
}
