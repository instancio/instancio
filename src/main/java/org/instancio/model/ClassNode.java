package org.instancio.model;

import org.instancio.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

// ClassNode can only have type variables
public class ClassNode extends Node {
    private static final Logger LOG = LoggerFactory.getLogger(ClassNode.class);

    public ClassNode(final NodeContext nodeContext,
                     final Class<?> klass,
                     final Type genericType,
                     final Node parent) {

        super(nodeContext, klass, genericType, parent);

        if (klass.getPackage() == null || klass.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
            setChildren(Collections.emptyList());
        } else {


            List<Node> children = Arrays.stream(klass.getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .filter(nodeContext::isUnvisited)
                    .map(field -> {
                        Type passedOnGenericType = ObjectUtils.defaultIfNull(genericType, field.getGenericType());
                        LOG.debug("Passing generic type to child field node: {}", passedOnGenericType);
                        return new FieldNode(nodeContext, field, field.getType(), passedOnGenericType, this);
                    })

                    .collect(toList());

            setChildren(children);
        }
    }

    public ClassNode(final NodeContext nodeContext,
                     final Class<?> klass) {

        this(nodeContext, klass, null, null);
    }


    @Override
    public String toString() {
        String s = "";
        s += "ClassNode: " + getKlass().getSimpleName() + "\n"
                + " -> typeMap: " + getNodeContext().getRootTypeMap() + "\n";
        return s;
    }


}
