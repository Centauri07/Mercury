package me.centauri07.mercury.school.task;

import lombok.Data;

import java.util.Date;

@Data
public class Task {
    private TaskType taskType;
    private String subject;
    private Date dueDate;
    private String description;
}
