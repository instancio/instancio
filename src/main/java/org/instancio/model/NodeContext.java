package org.instancio.model;

import java.lang.reflect.TypeVariable;
import java.util.Map;

public class NodeContext {

    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;

    public NodeContext(Map<TypeVariable<?>, Class<?>> rootTypeMap) {
        this.rootTypeMap = rootTypeMap;
    }

    public Map<TypeVariable<?>, Class<?>> getRootTypeMap() {
        return rootTypeMap;
    }
}
