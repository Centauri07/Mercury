package me.centauri07.mercury.school.subject;

import lombok.Data;
import me.centauri07.mercury.school.entity.Teacher;
import me.centauri07.mercury.school.task.Task;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.util.List;

@Data
public class Subject {
    private String name;
    private Teacher teacher;
    @BsonProperty(value = "google_meet_link")
    private String googleMeetLink;
    @BsonProperty(value = "canvas_course_link")
    private String canvasCourseLink;
    private List<Task> tasks;
}
