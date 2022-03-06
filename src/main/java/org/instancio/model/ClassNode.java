package org.instancio.model;

import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

// ClassNode can only have type variables
public class ClassNode {

    private static final String JAVA_PKG_PREFIX = "java";
    private final Map<TypeVariable<?>, Class<?>> typeMap;
    private final Class<?> klass;
    private final List<FieldNode> children;

    public ClassNode(final Class<?> klass, final Map<TypeVariable<?>, Class<?>> typeMap) {
        this.klass = klass;
        this.typeMap = validateTypeMap(klass, typeMap);

        if (klass.getPackage() == null || klass.getPackage().getName().startsWith(JAVA_PKG_PREFIX)) {
            this.children = Collections.emptyList();
        } else {
            this.children = Arrays.stream(klass.getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .map(field -> new FieldNode(
                            field, field.getType(), field.getGenericType(), /* parent field */ null,
                            typeMap, new HashSet<>())) // XXX visited per root field node or share across all?

                    .collect(toList());
        }
    }

    public Class<?> getKlass() {
        return klass;
    }

    public List<FieldNode> getChildren() {
        return children;
    }

    private static Map<TypeVariable<?>, Class<?>> validateTypeMap(Class<?> klass, Map<TypeVariable<?>, Class<?>> typeMap) {
        final TypeVariable<?>[] typeParameters = klass.getTypeParameters();
        final String msg = String.format("Type map should map each type parameter:\ntype map: %s\ntype parameters: %s",
                typeMap, Arrays.toString(typeParameters));

        // TODO revisit this check... previously '=='
        //Verify.isTrue(typeMap.size() >= typeParameters.length, msg);

//        for (TypeVariable<?> typeVar : typeParameters) {
//            Verify.isTrue(typeMap.containsKey(typeVar), msg);
//        }

        return Collections.unmodifiableMap(typeMap);
    }


    @Override
    public String toString() {
        String s = "";
        s += "ClassNode: " + klass.getSimpleName() + "\n"
                + " -> typeMap: " + typeMap + "\n";
        return s;
    }


}
