package experimental.reflection.nodes;

import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;

public class FieldNode {
    private static final String JAVA_PKG_PREFIX = "java";

    private final Field field;
    private final String[] typeVariables;
    private FieldNode parent;
    private List<FieldNode> children;
    private Map<GenericType, Class<?>> typeMap;

    // 1. class sun.reflect.generics.reflectiveObjects.ParameterizedTypeImpl  Foo<String> value;
    // 2. class sun.reflect.generics.reflectiveObjects.TypeVariableImpl       Foo<T> value;
    // 3. class java.lang.Class                                               Foo value;
    //
    // 1 => everything is bound... map TypeVar to T
    // 2 => need to bind... map TypeVar to T provided by user
    // 3 => non-generic class... simplest case

    public FieldNode(Field field) {
        this.field = Verify.notNull(field, "Field must not be null");
        this.typeVariables = getTypeVariables(field);
        this.typeMap = ReflectionUtils.getTypeMap(field);

        this.children = makeChildren();
    }

    private List<FieldNode> makeChildren() {
        Package fieldPackage = field.getType().getPackage();

        // Exclude JDK classes
        if (fieldPackage == null || fieldPackage.getName().startsWith(JAVA_PKG_PREFIX)) {
            return Collections.emptyList();
        }

        // which class to get fields from?
        // - field.getType()?
        // - pType?
//        Arrays.stream(klass.getDeclaredFields())
//                .filter(f -> !Modifier.isStatic(f.getModifiers()))
//                .collect(toList());
        return null;
    }

    private List<String> getUnboundTypeVariables() {
        return null;
    }

    /**
     * Returns type name of the declared field, e.g.
     *
     * <pre>{@code
     *   T someField;          // => T
     *   Pair<L,R> pair;       // => org.example.Pair<L, R>
     *   String str;           // => java.lang.String
     *   List<Phone> numbers;  // => java.util.List<org.example.Phone>
     * }</pre>
     */
    public String getTypeName() {
        return field.getGenericType().getTypeName();
    }

    public Deque<Class<?>> getParameterizedTypes() {
        return ReflectionUtils.getParameterizedTypes(field);
    }

    public boolean isGeneric() {
        Type type = field.getGenericType();
        return type instanceof ParameterizedType || type instanceof TypeVariable;
    }

    private static String[] getTypeVariables(Field field) {
        TypeVariable<?>[] typeParameters = field.getType().getTypeParameters();
        String[] typeVariables = new String[typeParameters.length];

        for (int i = 0; i < typeParameters.length; i++) {
            typeVariables[i] = typeParameters[i].getName();
        }
        return typeVariables;
    }

    @Override
    public String toString() {
        String s = "";
        s += "Field: " + field.getType().getSimpleName() + " " + field.getName() + "\n"
                + " -> typeVars: " + Arrays.toString(typeVariables) + "\n"
                + " -> pTypes: " + getParameterizedTypes() + "\n"
                + " -> typeName: " + getTypeName() + "\n"
                + " -> typeMap: " + typeMap + "\n";
        return s;
    }
}
