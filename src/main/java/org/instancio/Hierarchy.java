package org.instancio;

import java.util.IdentityHashMap;
import java.util.Map;

public class Hierarchy {

    /**
     * Maps object instance to its ancestor instance
     */
    private final Map<Object, Object> idMap = new IdentityHashMap<>();

    public void setAncestorOf(Object instance, Object ancestor) {
        idMap.put(instance, ancestor);
    }

    public Object getAncestorWithClass(Object instance, Class<?> ancestorClass) {
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
