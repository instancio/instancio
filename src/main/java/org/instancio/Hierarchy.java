package org.instancio;

import javax.annotation.Nullable;
import java.util.IdentityHashMap;
import java.util.Map;

public class Hierarchy {

    /**
     * Maps object instance to its ancestor instance
     */
    private final Map<Object, Object> idMap = new IdentityHashMap<>();

    public void setAncestorOf(final Object instance, final Object ancestor) {
        idMap.put(instance, ancestor);
    }

    public Object getAncestorWithClass(@Nullable final Object instance, final Class<?> ancestorClass) {
        if (instance == null) {
            return null;
        }

        Object ancestor = idMap.get(instance);

        while (ancestor != null) {
            if (ancestor.getClass() == ancestorClass) {
                return ancestor;
            }
            ancestor = idMap.get(ancestor);
        }

        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Hierarchy {\n");
        idMap.forEach((k, v) -> sb
                .append("  ")
                .append(k.getClass().getSimpleName())
                .append(" -> ")
                .append(v == null ? "null" : v.getClass().getSimpleName())
                .append("\n"));
        return sb.append("}").toString();
    }
}
