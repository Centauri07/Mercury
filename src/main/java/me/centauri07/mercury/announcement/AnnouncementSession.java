package me.centauri07.mercury.announcement;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

public class AnnouncementSession {
    private static final Map<String, AnnouncementSession> cache = new HashMap<>();
    private static final Map<String, Field> fieldCache = new HashMap<>();

    public static void addCache(Guild guild, User user, TextChannel channel, Role role) {
        cache.putIfAbsent(user.getId(), new AnnouncementSession(guild, user, channel, role));
    }

    public static AnnouncementSession getCache(String userId) {
        return cache.get(userId);
    }

    public static void removeCache(String userId) {
        cache.remove(userId);
    }

    public static void addFieldCache(String userId) {
        fieldCache.put(userId, new Field());
    }

    public static Field getFieldCache(String userId) {
        return fieldCache.get(userId);
    }

    public static void removeFieldCache(String userId) {
        fieldCache.remove(userId);
    }

    @Getter private final Guild guild;
    @Getter private final User user;
    @Getter private final Role role;
    @Getter private final TextChannel channel;
    @Getter private final EmbedBuilder embedBuilder = new EmbedBuilder();

    public AnnouncementSession(Guild guild, User user, TextChannel channel, Role role) {
        this.guild = guild;
        this.user = user;
        this.channel = channel;
        this.role = role;

        embedBuilder.setAuthor(user.getName(), null, user.getAvatarUrl());
        embedBuilder.setFooter("Announcement By: " + user.getName());
        embedBuilder.setTimestamp(new Date().toInstant());
    }

    private ScheduledFuture<?> timer;

    @Getter
    private boolean isField;
    @Getter
    private boolean isDescription;

    public void setIsField(boolean b) {
        this.isField = b;
    }

    public void setIsDescription(boolean b) {
        this.isDescription = b;
    }

    @Getter private Color color;

    public void setColor(Color color) {
        this.color = color;
        embedBuilder.setColor(color);
    }

    @Getter
    private String title;

    public void setTitle(String title) {
        embedBuilder.setTitle(title);
        this.title = title;
    }

    @Getter @Setter @Nullable
    private List<Field> fields = new ArrayList<>();

    @Getter @Nullable
    private String description;

    public void setDescription(String description) {
        embedBuilder.setDescription(description);
        this.description = description;
    }

    public void addField(Field field) {
        fields = fields == null ? new ArrayList<>() : fields;
        fields.add(field);

        embedBuilder.addField(field.getTitle(), field.getDescription(), false);
    }

    public void startTimer() {
        timer = Executors.newSingleThreadScheduledExecutor().schedule(
                () -> {
                    removeCache(user.getId());
                    removeFieldCache(user.getId());
                }, 3, TimeUnit.MINUTES
        );
    }

    public void resetTimer() {
        stopTimer();
        startTimer();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel(true);
        }
    }
}
