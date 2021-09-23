package me.centauri07.mercury.util;

import lombok.Getter;

public enum Color {
    ORANGE(java.awt.Color.ORANGE),
    GREEN(java.awt.Color.GREEN),
    YELLOW(java.awt.Color.YELLOW),
    BLUE(java.awt.Color.BLUE),
    CYAN(java.awt.Color.CYAN),
    MAGENTA(java.awt.Color.MAGENTA),
    PINK(java.awt.Color.PINK);

    @Getter
    private final java.awt.Color color;

    Color(java.awt.Color color) {
        this.color = color;
    }
}
