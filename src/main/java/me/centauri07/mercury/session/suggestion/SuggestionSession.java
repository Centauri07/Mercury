package me.centauri07.mercury.session.suggestion;

import me.centauri07.mercury.session.Session;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

public class SuggestionSession extends Session {
    public SuggestionSession(MessageChannel channel, Member member) {
        super(channel, member);
    }

    @Override
    public void onSessionFinish() {

    }

    @Override
    public void onSessionExpire() {

    }
}