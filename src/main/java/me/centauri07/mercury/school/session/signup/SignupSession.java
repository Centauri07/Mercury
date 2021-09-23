package me.centauri07.mercury.school.session.signup;

import lombok.Getter;
import lombok.Setter;
import me.centauri07.mercury.school.entity.Student;
import me.centauri07.mercury.school.section.Section;
import net.dv8tion.jda.api.entities.Member;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class SignupSession {
    private static final Map<String, SignupSession> cache = new HashMap<>();

    public static void addCache(Member member) {
        cache.putIfAbsent(member.getId(), new SignupSession(member));
    }

    public static SignupSession getCache(String userId) {
        return cache.get(userId);
    }

    public static void removeCache(String userId) {
        cache.remove(userId);
    }

    @Getter
    private final Member member;
    @Getter
    private final Student student = new Student();
    @Getter @Setter
    private Section section;

    public SignupSession(Member member) {
        this.member = member;
        student.setDiscordId(member.getId());
    }

    private ScheduledFuture<?> timer;

    public void startTimer() {
        timer = Executors.newSingleThreadScheduledExecutor().schedule(
                () -> removeCache(member.getId()), 3, TimeUnit.MINUTES
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
