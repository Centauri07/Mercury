package me.centauri07.mercury.school.section;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.school.entity.Student;
import me.centauri07.mercury.util.Color;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class SectionManager {
    @Getter
    private final List<Section> sections = new ArrayList<>();

    @Getter
    private final MongoCollection<SectionInformation> sectionsInfo;

    public SectionManager() {
        if (!Mercury.getMongoConnection().collectionExists(Mercury.getMongoConnection().getSchoolDatabase(), "sections_info")) {
            Mercury.getMongoConnection().getSchoolDatabase().createCollection("sections_info");
        }

        sectionsInfo = Mercury.getMongoConnection().getSchoolDatabase().getCollection("sections_info", SectionInformation.class);

        loadSections();
    }

    public void loadSections() {
        for (SectionInformation sectionInformation : sectionsInfo.find()) {
            Section section = new Section(sectionInformation.getName(), Objects.requireNonNull(Mercury.getJda()
                    .getGuildById(sectionInformation.getGuildId())));
            section.setSectionInformation(sectionInformation);
            addSection(section);
        }
    }

    public void createSection(Guild guild, String name) {
        if (getSection(guild, name) == null) {
            Section section = new Section(name, guild);
            section.setUp(this);
            SectionInformation sectionInformation = new SectionInformation();
            sectionInformation.setName(name);
            sectionInformation.setGuildId(guild.getId());

            guild.createRole().setName(name).setColor(Color.values()[new Random().nextInt(Color.values().length)].getColor()).queue(
                    role -> sectionInformation.setRoleId(role.getId())
            );

            AtomicReference<Category> sectionCategory = new AtomicReference<>();

            guild.createCategory(name).queue(
                    category -> {
                        sectionCategory.set(category);
                        category.createTextChannel("announcement📣")
                                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.MESSAGE_WRITE)).queue(
                                    channel -> sectionInformation.setAnnouncementChannelId(channel.getId())
                        );
                        category.createTextChannel("tasks📚")
                                .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.MESSAGE_WRITE)).queue(
                                        channel -> sectionInformation.setTasksChannelId(channel.getId())
                        );
                        category.createTextChannel("discussion💬").queue();
                        category.createVoiceChannel("meeting🎙️").queue();
                    }
            );

            Executors.newSingleThreadScheduledExecutor().schedule(
                    () -> {
                        section.setSectionInformation(sectionInformation);

                        sectionsInfo.insertOne(sectionInformation);

                        sectionCategory.get().putPermissionOverride(section.getRole()).queue();

                        addSection(section);
                    }, 5, TimeUnit.SECONDS
            );
        }
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public void removeSection(Guild guild, String name) {
        Section section = getSection(guild, name);

        if (section != null) {
            section.getDatabase().drop();

            if (!section.getGuild().getCategoriesByName(name, true).isEmpty()) {
                Category category = getSection(guild, name).getGuild().getCategoriesByName(name, false).get(0);

                if (category != null) {
                    for (GuildChannel channel : category.getChannels()) {
                        channel.delete().queue();
                    }

                    category.delete().queue();
                }

                if (section.getRole() != null) {
                    section.getRole().delete().queue();
                }

                sectionsInfo.findOneAndDelete(Filters.eq("name", name));
                sections.remove(section);
            }
        }
    }

    public Section getSection(Guild guild, String name) {
        return sections.stream().filter(section -> section.getGuild().equals(guild) && section.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public List<Section> getSectionsByGuild(Guild guild) {
        return sections.stream().filter(section -> section.getGuild() == guild).collect(Collectors.toList());
    }

    public Section getSectionByMember(Member member) {
        return sections.stream().filter(section -> section.getStudents().stream().map(Student::getDiscordId)
                .collect(Collectors.toList()).contains(member.getId())).findFirst().orElse(null);
    }

    public Section getSectionByStudent(Guild guild, String id) {
        return getSectionsByGuild(guild).stream().filter(section -> section.getStudent(id) != null).findFirst().orElse(null);
    }

    public Section getSectionByMemberInGuild(Guild guild, Member member) {
        return getSectionsByGuild(guild).stream().filter(section -> section.getStudents().stream().map(Student::getDiscordId)
                .collect(Collectors.toList()).contains(member.getId())).findFirst().orElse(null);
    }

    public Section getSectionByUser(User user) {
        return sections.stream().filter(section -> section.getStudents().stream().map(Student::getDiscordId)
                .collect(Collectors.toList()).contains(user.getId())).findFirst().orElse(null);
    }
}
