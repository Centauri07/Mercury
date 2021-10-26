package me.centauri07.mercury;

import lombok.Getter;
import me.centauri07.command.CommandManager;
import me.centauri07.mercury.announcement.command.AnnounceCommand;
import me.centauri07.mercury.announcement.listener.AnnouncementListener;
import me.centauri07.mercury.command.HelpCommand;
import me.centauri07.mercury.command.PingCommand;
import me.centauri07.mercury.command.PurgeCommand;
import me.centauri07.mercury.database.MongoConnection;
import me.centauri07.mercury.listener.GuildStudentJoinListener;
import me.centauri07.mercury.school.listener.SignupListener;
import me.centauri07.mercury.school.section.SectionManager;
import me.centauri07.mercury.session.SessionListener;
import me.centauri07.mercury.util.PrivateUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

import javax.security.auth.login.LoginException;

public class Mercury {
    @Getter static private JDA jda;
    @Getter static private SectionManager sectionManager;
    @Getter static private MongoConnection mongoConnection;
    @Getter static private CommandManager commandManager;

    public static void main(String[] args) throws LoginException, InterruptedException {
        jda = JDABuilder.createDefault(PrivateUtil.TOKEN).enableIntents(GatewayIntent.GUILD_MEMBERS).build();
        jda.awaitReady();

        mongoConnection = new MongoConnection();

        if (!mongoConnection.collectionExists(mongoConnection.getMongoClient().getDatabase("school"), "sections_info"))
            mongoConnection.getMongoClient().getDatabase("school").createCollection("sections_info");

        sectionManager = new SectionManager();

        jda.addEventListener(
                new AnnouncementListener(),
                new SignupListener(),
                new GuildStudentJoinListener(),
                new SessionListener()
        );

        commandManager = new CommandManager(jda);
        CommandManager.setPrefix("$");
        commandManager.registerTextCommand(new PingCommand());
        commandManager.registerTextCommand(new PurgeCommand());
        commandManager.registerTextCommand(new AnnounceCommand());
        commandManager.registerTextCommand(new HelpCommand());
    }
}
