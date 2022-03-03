package org.instancio;

import java.lang.reflect.Field;
import java.util.Deque;
import java.util.Map;
import java.util.StringJoiner;

class CreateItem {

    private final Field field;
    private final Object owner;
    private final Map<String, Deque<Class<?>>> typeMap;

    public CreateItem(Field field, Object owner, Map<String, Deque<Class<?>>> typeMap) {
        this.field = field;
        this.owner = owner;
        this.typeMap = typeMap;
    }

    public Field getField() {
        return field;
    }

    public Object getOwner() {
        return owner;
    }

    public Map<String, Deque<Class<?>>> getTypeMap() {
        return typeMap;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", CreateItem.class.getSimpleName() + "[", "]")
                .add("field=" + field)
                .add("owner=" + owner.getClass().getName())
                .add("typeMap=" + typeMap)
                .toString();
    }
}
