package org.instancio.model;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static java.util.stream.Collectors.toList;

// ClassNode can only have type variables
public class ClassNode extends Node {

    public ClassNode(final NodeContext nodeContext,
                     final Class<?> klass,
                     final Type genericType) {

        super(nodeContext, klass, genericType);
    }

    public ClassNode(final NodeContext nodeContext,
                     final Class<?> klass) {

        this(nodeContext, klass, null);

        if (klass.getPackage() == null || klass.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
            setChildren(Collections.emptyList());
        } else {
            List<Node> children = Arrays.stream(klass.getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .map(field -> new FieldNode(
                            nodeContext, field, field.getType(), field.getGenericType(), /* parent field */ null,
                            new HashSet<>())) // XXX visited per root field node or share across all?

                    .collect(toList());

            setChildren(children);
        }
    }


    @Override
    public String toString() {
        String s = "";
        s += "ClassNode: " + getKlass().getSimpleName() + "\n"
                + " -> typeMap: " + getNodeContext().getRootTypeMap() + "\n";
        return s;
    }


}
