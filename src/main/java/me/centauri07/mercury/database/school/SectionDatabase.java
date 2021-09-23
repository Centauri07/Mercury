package me.centauri07.mercury.database.school;

import com.mongodb.client.MongoCollection;
import lombok.Getter;
import me.centauri07.mercury.Mercury;
import me.centauri07.mercury.school.entity.Student;
import me.centauri07.mercury.school.section.Section;
import me.centauri07.mercury.school.subject.Subject;

import java.util.ArrayList;
import java.util.List;

public class SectionDatabase {
    @Getter
    private final Section section;

    @Getter
    private MongoCollection<Student> sectionStudents;
    @Getter
    private MongoCollection<Subject> sectionSubjects;

    public SectionDatabase(Section section) {
        this.section = section;
        createOrLoad();
        loadData();
    }

    public void loadData() {
        List<Student> students = new ArrayList<>();
        for (Student student : sectionStudents.find()) {
            students.add(student);
        }
        section.setStudents(students);

        List<Subject> subjects = new ArrayList<>();
        for (Subject subject : sectionSubjects.find()) {
            subjects.add(subject);
        }
        section.setSubjects(subjects);
    }

    public void createOrLoad() {
        if (!Mercury.getMongoConnection().collectionExists(section.getDatabase(), "students"))
            section.getDatabase().createCollection("students");
        sectionStudents = section.getDatabase().getCollection("students", Student.class);

        if (!Mercury.getMongoConnection().collectionExists(section.getDatabase(), "subjects"))
            section.getDatabase().createCollection("subjects");
        sectionSubjects =section.getDatabase().getCollection("subjects", Subject.class);
    }
}