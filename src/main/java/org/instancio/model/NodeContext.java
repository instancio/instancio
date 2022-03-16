package org.instancio.model;

import org.instancio.reflection.DeclaredAndInheritedFieldsCollector;
import org.instancio.reflection.FieldCollector;

import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// TODO rename
public class NodeContext {

    private final FieldCollector fieldCollector = new DeclaredAndInheritedFieldsCollector();
    private final Set<Node> visited = new HashSet<>();
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;

    public NodeContext(final Map<TypeVariable<?>, Class<?>> rootTypeMap) {
        this.rootTypeMap = Collections.unmodifiableMap(rootTypeMap);
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }

    public void visited(final Node node) {
        visited.add(node);
    }

    public boolean isUnvisited(final Node node) {
        return !visited.contains(node);
    }

    public FieldCollector getFieldCollector() {
        return fieldCollector;
    }
}
