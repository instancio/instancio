package org.instancio.model;

import java.lang.reflect.TypeVariable;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NodeContext {

    private final Set<Node> visited = new HashSet<>();
    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;

    public NodeContext(final Map<TypeVariable<?>, Class<?>> rootTypeMap) {
        this.rootTypeMap = rootTypeMap;
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
}
