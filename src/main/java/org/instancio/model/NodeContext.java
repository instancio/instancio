package org.instancio.model;

import java.lang.reflect.Field;
import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeContext {

    private final Set<Field> visited = new HashSet<>();
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;

    public NodeContext(final Map<TypeVariable<?>, Class<?>> rootTypeMap) {
        this.rootTypeMap = rootTypeMap;
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }

    public void visited(final Field field) { // XXX use Nodes or Fields?
        visited.add(field);
    }

    public boolean isUnvisited(final Field field) {
        return !visited.contains(field);
    }
}
