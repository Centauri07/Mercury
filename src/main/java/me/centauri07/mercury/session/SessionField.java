package me.centauri07.mercury.session;

import lombok.RequiredArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class SessionField {
    public final Object object;
    public final Field field;
    public final String id;
    public final String name;
    public final boolean required;
    public boolean isAcknowledge = false;
    public List<SessionField> subFields = new ArrayList<>();
}
