package me.centauri07.mercury.session;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class Session {
    private static final List<Session> SESSIONS = new ArrayList<>();

    public static Session getSession(Member member) {
        return SESSIONS.stream().filter(session -> session.member.equals(member)).findFirst().orElse(null);
    }

    public static Session getSession(User user) {
        return SESSIONS.stream().filter(session -> session.member.getUser().equals(user)).findFirst().orElse(null);
    }

    public static boolean hasSession(Member member) {
        return getSession(member) != null;
    }

    public static <T extends Session> void createSession(T session) {
        SESSIONS.add(session);
    }

    public static void removeSession(Member member) {
        Session session = getSession(member);
        session.stopTimer();
        SESSIONS.remove(session);
    }

    public static void removeSession(User user) {
        Session session = getSession(user);
        session.stopTimer();
        SESSIONS.remove(session);
    }

    public final List<SessionField> fields = new ArrayList<>();
    public final Member member;

    public MessageChannel channel;

    public SessionField sessionField;

    public Session(MessageChannel channel, Member member) {
        SESSIONS.add(this);

        this.channel = channel;
        this.member = member;

        for (Field field : Arrays.stream(getClass().getDeclaredFields()).filter(
                field -> field.isAnnotationPresent(me.centauri07.mercury.session.Field.class)
        ).collect(Collectors.toList())) {
            field.setAccessible(true);
            me.centauri07.mercury.session.Field sessionField = field.getDeclaredAnnotation(me.centauri07.mercury.session.Field.class);
            try {
                fields.add(new SessionField(field.get(this), field, sessionField.id(), sessionField.name(), sessionField.required()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (!field.getType().isPrimitive() && !field.getType().isAssignableFrom(String.class)) {
                field.setAccessible(true);
            }
        }

        for (SessionField sessionField : fields) {
            prepareField(sessionField);
        }

        sessionField = findSessionField(fields.stream().filter(field -> !field.isAcknowledge).findFirst().orElse(null));

        channel.sendMessageEmbeds(new EmbedBuilder().setColor(Color.CYAN).setDescription("Enter " + sessionField.name).build()).queue();
    }

    private ScheduledFuture<?> timer;

    public void startTimer() {
        timer = Executors.newSingleThreadScheduledExecutor().schedule(
                () -> {
                    SESSIONS.remove(getSession(member));
                    stopTimer();
                    onSessionExpire();
                }, 3, TimeUnit.MINUTES
        );
    }

    private void resetTimer() {
        stopTimer();
        startTimer();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel(true);
        }
    }

    public void fireEvent(Message message) {
        sessionField = findSessionField(fields.stream().filter(field -> !field.isAcknowledge).findFirst().orElse(null));

        if (sessionField == null) {
            onSessionFinish();
            return;
        }

        java.lang.reflect.Field field = sessionField.field;

        Object object = sessionField.object;

        resetTimer();

        field.setAccessible(true);

        if (object == null) return;

        if (field.getType().isAssignableFrom(Number.class)) {
            try {
                field.setInt(object, Integer.parseInt(message.getContentRaw()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (field.getType().isAssignableFrom(String.class)) {
            try {
                field.set(this, message.getContentRaw());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (field.getType().isAssignableFrom(Boolean.class)) {
            try {
                field.setBoolean(this, Boolean.parseBoolean(message.getContentRaw()));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (field.getType().isAssignableFrom(Character.class)) {
            try {
                field.setChar(this, message.getContentRaw().charAt(0));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } else if (field.getType().isAssignableFrom(List.class)) {
            Type listType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
            if (Arrays.stream(listType.getClass().getDeclaredFields()).noneMatch(it -> it.getDeclaredAnnotation(me.centauri07.mercury.session.Field.class) != null)) {
                try {
                    throw new IllegalAccessException(listType.getTypeName() + " cannot be parsed");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } else if (field.getType().isArray()) {

        }
        try {
            if (field.get(object) != null) {
                sessionField.isAcknowledge = true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        SessionField newField = fields.stream().filter(it -> !it.isAcknowledge).findFirst().orElse(null);

        if (newField == null) {
            removeSession(member);
            onSessionFinish();
            return;
        }

        channel.sendMessageEmbeds(
                new EmbedBuilder().setColor(Color.CYAN).setDescription("Enter " + newField.name).build()
        ).queue();

    }

    public void prepareField(SessionField sessionField) {
        if (!sessionField.field.getType().isPrimitive() &&
                !sessionField.field.getType().isAssignableFrom(String.class) &&
                !sessionField.field.getType().isAssignableFrom(Collection.class)) {
            System.out.println(sessionField.field.getType());
            for (Field field : sessionField.field.getType().getDeclaredFields()) {
                if (field.isAnnotationPresent(me.centauri07.mercury.session.Field.class)) {
                    me.centauri07.mercury.session.Field childField = field.getDeclaredAnnotation(me.centauri07.mercury.session.Field.class);
                    SessionField childSessionField;
                    try {
                        childSessionField = new SessionField(field.get(sessionField.object), field, childField.name(), childField.id(), childField.required());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        return;
                    }
                    sessionField.subFields.add(childSessionField);
                    if (!field.getType().isPrimitive() && !(field.getType().isAssignableFrom(String.class))) {
                        prepareField(childSessionField);
                    }
                }
            }
        }
    }

    public SessionField findSessionField(SessionField sessionField) {
        if (sessionField != null) {
            if (!sessionField.field.getType().isPrimitive() &&
                    !sessionField.field.getType().isAssignableFrom(String.class) &&
                    !sessionField.field.getType().isAssignableFrom(Collection.class)) {
                if (Arrays.stream(sessionField.subFields.getClass().getDeclaredFields()).noneMatch(field -> field.isAnnotationPresent(me.centauri07.mercury.session.Field.class)))
                    throw new NullPointerException("Object doesn't have fields!");
                sessionField = findSessionField(sessionField.subFields.stream().filter(field -> !field.isAcknowledge).findFirst().orElse(null));
            }
        }

        return sessionField;
    }

    public abstract void onSessionFinish();
    public abstract void onSessionExpire();
}
