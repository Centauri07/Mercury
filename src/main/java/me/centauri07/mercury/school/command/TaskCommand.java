package me.centauri07.mercury.school.command;

import me.centauri07.command.CommandInformation;
import me.centauri07.command.attributes.RequiredArgsRange;
import me.centauri07.command.event.TextCommandEvent;
import me.centauri07.command.text.TextCommandHandler;
import org.jetbrains.annotations.NotNull;

@RequiredArgsRange(lowerBound = 0, upperBound = 2)
@CommandInformation(name = "task", description = "Tasks of your section", usage = "task [create | remove]")
public class TaskCommand extends TextCommandHandler {
    @Override
    public void perform(@NotNull TextCommandEvent event) {
        // TODO like announcement system

        // Message: Please select a subject on where you want to add a task | Button: Subjects
        // Message: Please enter a due date (Format: 2020-5-31)
        // Message: Please enter a description
    }
}
