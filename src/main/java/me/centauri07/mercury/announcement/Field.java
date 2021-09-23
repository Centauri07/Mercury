package me.centauri07.mercury.announcement;

import lombok.Getter;
import lombok.Setter;

public class Field {
    @Getter @Setter
    private String title;

    @Getter @Setter
    private String description;

    public boolean hasTitle() {
        return title != null;
    }

    public boolean hasDescription() {
        return description != null;
    }
}
