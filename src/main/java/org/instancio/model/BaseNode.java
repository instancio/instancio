package org.instancio.model;

import java.lang.reflect.Type;

abstract class BaseNode extends Node {
    static final String JAVA_PKG_PREFIX = "java";

    private final Class<?> klass;
    private final Type genericType;

    public BaseNode(
            final NodeContext nodeContext,
            final Class<?> klass,
            final Type genericType,
            final Node parent) {

        super(nodeContext, parent);

        this.klass = klass;
        this.genericType = genericType;
    }

    public Class<?> getKlass() {
        return klass;
    }

    public Type getGenericType() {
        return genericType;
    }
}
