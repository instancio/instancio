package org.instancio.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class BaseNode extends Node {
    private static final Logger LOG = LoggerFactory.getLogger(BaseNode.class);
    static final String JAVA_PKG_PREFIX = "java";

    private final Class<?> klass;
    private final Type genericType;
    private final Map<TypeVariable<?>, Type> typeMap;
    private final List<PType> nestedTypes = new ArrayList<>(); // XXX can delete?

    public BaseNode(
            final NodeContext nodeContext,
            final Class<?> klass,
            final Type genericType,
            final Node parent) {

        super(nodeContext, parent);

        this.klass = klass;
        this.genericType = genericType;
        this.typeMap = getTypeMap(klass, genericType);
    }

    public Class<?> getKlass() {
        return klass;
    }

    public Type getGenericType() {
        return genericType;
    }

    public Map<TypeVariable<?>, Type> getTypeMap() {
        return typeMap;
    }

    protected List<PType> getNestedTypes() {
        return nestedTypes;
    }

    private Map<TypeVariable<?>, Type> getTypeMap(Class<?> declaringClass, final Type genericType) {
        if (declaringClass == null) {
            return Collections.emptyMap();
        }

        // FIXME test hack
        if (genericType instanceof ParameterizedType) {
            declaringClass = (Class<?>) ((ParameterizedType) genericType).getRawType();
        }

        final Map<TypeVariable<?>, Type> map = new HashMap<>();
        final TypeVariable<?>[] typeVars = declaringClass.getTypeParameters();

        if (genericType instanceof ParameterizedType) {

            ParameterizedType pType = (ParameterizedType) genericType;
            Type[] typeArgs = pType.getActualTypeArguments();

//            LOG.debug("pType: {}", pType);
//            LOG.debug("actualTypeArguments: {}", Arrays.toString(typeArgs));

            for (int i = 0; i < typeArgs.length; i++) {
                TypeVariable<?> tvar = typeVars[i];
                Type actualType = typeArgs[i];

                //LOG.debug(" --> tvar: {}, actualType: {}", tvar, actualType);
                // XXX typeMap should use Type objects as keys? what about user rootTypeMap?
                //  Problem is using strings can overwrite values when string keys are the same,
                //  even though they might represent different Type objects...
                //  Need to isolate this scenario and test it.
                if (actualType instanceof TypeVariable) {

                    map.put(tvar, actualType);

                    if (getRootTypeMap().containsKey(actualType)) {
                        map.put((TypeVariable<?>) actualType, getRootTypeMap().get(actualType));
                    }
                } else if (actualType instanceof ParameterizedType) {
                    Class<?> c = (Class<?>) ((ParameterizedType) actualType).getRawType();
                    ParameterizedType nestedPType = (ParameterizedType) actualType;

                    map.put(tvar, c);

                    nestedTypes.add(new PType(c, nestedPType));
                } else if (actualType instanceof Class) {
                    map.put(tvar, actualType);
                } else {
                    throw new IllegalStateException("Unhandled type: " + actualType);
                }
            }
        } else {
            LOG.debug("No generic info for declaringClass: {}, genericType: {}", declaringClass, genericType);
        }

        return map;
    }
}
