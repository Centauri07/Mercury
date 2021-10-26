package me.centauri07.mercury.util;

import lombok.Getter;

import java.lang.reflect.Field;

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

    public static java.awt.Color getColor(String name) {
        Field[] declaredFields = java.awt.Color.class.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            if (field.getName().equalsIgnoreCase(name)) {
                try {
                    if (field.get(field.getClass()) instanceof java.awt.Color) {
                        return (java.awt.Color) field.get(field.getClass());
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    Color(java.awt.Color color) {
        this.color = color;
    }
}
