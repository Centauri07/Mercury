package me.centauri07.mercury.session.announcement;

import lombok.Getter;
import lombok.Setter;

public class Field {
    @me.centauri07.mercury.session.Field(
            id = "announcement-field-title",
            name = "Announcement Field Title"
    )
    public String title;


    @me.centauri07.mercury.session.Field(
            id = "announcement-field-description",
            name = "Announcement Field Description"
    )
    public String description;
}
