package me.centauri07.mercury.util;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;

@UtilityClass
public class ReflectionUtil {
    @SneakyThrows
    public Field getField(Class<?> clazz, String name) {
        String[] nodes = name.split("\\.");

        Field currentField = null;

        for (String currentNode : nodes) {
            if (currentField != null) {
                currentField = currentField.getType().getDeclaredField(currentNode);
            } else {
                currentField = clazz.getDeclaredField(currentNode);
            }
        }

        return currentField;
    }
}
