package me.centauri07.mercury.announcement;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.*;

public class AnnouncementSession {
    private static final Map<String, AnnouncementSession> cache = new HashMap<>();
    private static final Map<String, Field> fieldCache = new HashMap<>();

    public static void addCache(User user, Role role, TextChannel channel) {
        cache.putIfAbsent(user.getId(), new AnnouncementSession(user, role, channel));
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

    @Getter private final User user;
    @Getter private final Role role;
    @Getter private final TextChannel channel;
    @Getter private final EmbedBuilder embedBuilder = new EmbedBuilder();

    public AnnouncementSession(User user, Role role, TextChannel channel) {
        this.user = user;
        this.role = role;
        this.channel = channel;

        embedBuilder.setAuthor(user.getName(), null, user.getAvatarUrl());
        embedBuilder.setFooter("Announcement By: " + user.getName());
        embedBuilder.setTimestamp(new Date().toInstant());
    }

    private ScheduledFuture<?> timer;

    @Getter
    private boolean isField = false;
    @Getter
    private boolean isDescription = false;

    public void setIsField(boolean b) {
        this.isField = b;
    }

    public void setIsDescription(boolean b) {
        this.isDescription = b;
    }

    @Getter @Setter
    private String title;

    @Getter @Setter @Nullable
    private List<Field> fields = new ArrayList<>();

    @Getter @Setter @Nullable
    private String description;

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
