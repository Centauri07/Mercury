package me.centauri07.mercury.session.announcement;

import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.components.Button;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.*;

public class Announcement {
    private static final Map<String, Announcement> cache = new HashMap<>();
    private static final Map<String, Field> fieldCache = new HashMap<>();
    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void addCache(Guild guild, User user, MessageChannel channel, Role role, TextChannel usageChannel) {
        cache.putIfAbsent(user.getId(), new Announcement(guild, user, channel, role, usageChannel));
    }

    public static Announcement getCache(String userId) {
        return cache.get(userId);
    }

    public static void removeCache(String userId) {
        getCache(userId).stopTimer();
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
    @Getter private final MessageChannel channel;
    @Getter private final EmbedBuilder embedBuilder = new EmbedBuilder();
    @Getter private final TextChannel usageChannel;

    public Announcement(Guild guild, User user, MessageChannel channel, Role role, TextChannel usageChannel) {
        this.guild = guild;
        this.user = user;
        this.channel = channel;
        this.role = role;
        this.usageChannel = usageChannel;

        embedBuilder.setAuthor(user.getName(), null, user.getAvatarUrl());
    }

    public final Button DESCRIPTION = Button.primary(UUID.randomUUID().toString(), "\uD83D\uDCF0 Description");
    public final Button FIELDS = Button.primary(UUID.randomUUID().toString(), "\uD83D\uDCD2 Fields");
    public final Button DESCRIPTION_AND_FIELDS = Button.primary(UUID.randomUUID().toString(), "\uD83D\uDCDA Description and Fields");

    public final Button CONFIRM = Button.success(UUID.randomUUID().toString(), "✅ Confirm");
    public final Button CANCEL = Button.danger(UUID.randomUUID().toString(), "❌ Cancel");

    public final Button ADD_FIELD = Button.primary(UUID.randomUUID().toString(), "➕ Add Field");

    public final Button IMAGE_AGREE = Button.success(UUID.randomUUID().toString(), "✅ Yes");
    public final Button IMAGE_DISAGREE = Button.danger(UUID.randomUUID().toString(), "❌ No");

    private ScheduledFuture<?> timer;

    @Getter
    private boolean isField;
    @Getter
    private boolean isDescription;
    @Getter
    private boolean isImage;

    public void setIsField(boolean b) {
        this.isField = b;
    }

    public void setIsDescription(boolean b) {
        this.isDescription = b;
    }

    public void setIsImage(boolean b) {
        this.isImage = b;
    }

    @Getter
    private String url;

    public void setImageUrl(String url) {
        this.url = url;
        embedBuilder.setImage(url);
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

        embedBuilder.addField(field.title, field.description, false);
    }

    public void startTimer() {
        timer = executorService.schedule(
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
