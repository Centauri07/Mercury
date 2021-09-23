package me.centauri07.mercury.school.section;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Data;
import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.database.school.SectionDatabase;
import me.centauri07.mercury.school.entity.Student;
import me.centauri07.mercury.school.subject.Subject;
import me.centauri07.mercury.school.task.Task;
import me.centauri07.mercury.util.Color;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;

import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Data
public class Section {
    private final MongoDatabase database;

    private final String name;

    private final Guild guild;

    private TextChannel announcementChannel;
    private TextChannel taskChannel;
    private Role role;

    private List<Student> students;
    private List<Subject> subjects;
    private SectionInformation sectionInformation;

    private SectionDatabase sectionDatabase;

    public Section(String name, Guild guild) {
        this.name = name;
        this.guild = guild;

        database = Mercury.getMongoConnection().getMongoClient().getDatabase(name);

        sectionDatabase = new SectionDatabase(this);

        loadInformation();
    }

    public void addSubject(Subject subject) {
        sectionDatabase.getSectionSubjects().insertOne(subject);
        subjects.add(subject);
    }

    public void addStudent(Student student) {
        sectionDatabase.getSectionStudents().insertOne(student);
        students.add(student);
        guild.addRoleToMember(Objects.requireNonNull(guild.getMemberById(student.getDiscordId())), role).queue();
    }

    public void addTask(Subject subject, Task task) {
        subject.getTasks().remove(task);
        sectionDatabase.getSectionSubjects().findOneAndReplace(Filters.eq("name", subject.getName()), subject);
    }

    public void removeSubject(Subject subject) {
        subjects.remove(subject);
        sectionDatabase.getSectionSubjects().deleteOne(Filters.eq("name", subject.getName()));
    }

    public void removeStudent(Student student) {
        sectionDatabase.getSectionStudents().insertOne(student);
        students.remove(student);
    }

    public void removeTask(Subject subject, Task task) {
        subject.getTasks().remove(task);
        sectionDatabase.getSectionSubjects().findOneAndReplace(Filters.eq("name", subject.getName()), subject);
    }

    public Subject getSubject(String subjectName) {
        return subjects.stream().filter(subject -> subject.getName().equals(subjectName)).findFirst().orElse(null);
    }

    public Student getStudent(Member member) {
        return students.stream().filter(student -> student.getDiscordId().equals(member.getId())).findFirst().orElse(null);
    }

    public Student getStudent(String id) {
        return students.stream().filter(student -> student.getDiscordId().equals(id)).findFirst().orElse(null);
    }

    public void setUp(SectionManager sectionManager) {
        sectionInformation = new SectionInformation();

        sectionManager.getSectionsInfo().findOneAndDelete(Filters.eq("name", sectionInformation.getName()));

        sectionInformation.setName(name);
        sectionInformation.setGuildId(guild.getId());

        guild.createRole().setName(name).setColor(Color.values()[new Random().nextInt(Color.values().length)].getColor()).queue(
                role -> sectionInformation.setRoleId(role.getId())
        );

        guild.createCategory(name).addPermissionOverride(
                getRole(), EnumSet.of(Permission.MESSAGE_READ), null
        ).addPermissionOverride(
                getGuild().getPublicRole(), null, EnumSet.of(Permission.MESSAGE_READ)
        ).queue(
                category -> {
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
                    setSectionInformation(sectionInformation);

                    sectionManager.getSectionsInfo().insertOne(sectionInformation);

                    sectionManager.addSection(this);
                }, 5, TimeUnit.SECONDS
        );
    }

    public void loadInformation() {
        if (sectionInformation.getRoleId() == null || guild.getRoleById(sectionInformation.getRoleId()) == null) {
            guild.createRole().setColor(Color.values()[new Random().nextInt(Color.values().length)].getColor()).setName(name).queue(
                    sectionRole -> {
                        role = sectionRole;
                        sectionInformation.setRoleId(sectionRole.getId());
                    }
            );
        }

        if (guild.getCategoriesByName(name, false).isEmpty()) {
            guild.createCategory(name).addPermissionOverride(role, EnumSet.of(Permission.MESSAGE_READ), null)
                    .addPermissionOverride(guild.getPublicRole(), null, EnumSet.of(Permission.MESSAGE_READ)).queue(
                            category -> {
                                if (sectionInformation.getAnnouncementChannelId() == null || guild.getTextChannelById(sectionInformation.getAnnouncementChannelId()) == null) {
                                    category.createTextChannel("announcement📣").addPermissionOverride(
                                            guild.getPublicRole(),
                                            null,
                                            EnumSet.of(Permission.MESSAGE_WRITE)
                                    ).queue(
                                            channel -> {
                                                announcementChannel = channel;
                                                sectionInformation.setAnnouncementChannelId(channel.getId());
                                            }
                                    );
                                }

                                if (sectionInformation.getTasksChannelId() == null || guild.getTextChannelById(sectionInformation.getTasksChannelId()) == null) {
                                    category.createTextChannel("tasks📚").addPermissionOverride(
                                            guild.getPublicRole(),
                                            null,
                                            EnumSet.of(Permission.MESSAGE_WRITE)
                                    ).queue(
                                            channel -> {
                                                taskChannel = channel;
                                                sectionInformation.setTasksChannelId(channel.getId());
                                            }
                                    );
                                }
                            }
            );
        }
    }
}
