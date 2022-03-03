package experimental.reflection.nodes;

import java.util.Objects;

/**
 * Represents a generic class with its type variable. If a class has more
 * than one type variable, such as {@code Map<K,V>}, then there will be
 * a {@code GenericType} instance per each type variable.
 *
 * <pre>{@code
 *  List<E>  => {List.class, "E"}
 *  Map<K,V> => {Map.class, "K"}, {Map.class, "V"}
 * }</pre>
 */
public class GenericType {
    private final Class<?> declaringClass;
    private final String typeVariable;

    private GenericType(Class<?> declaringClass, String typeVariable) {
        this.declaringClass = declaringClass;
        this.typeVariable = typeVariable;
    }

    public static GenericType with(Class<?> declaringClass, String typeVariable) {
        return new GenericType(declaringClass, typeVariable);
    }

    public Class<?> getDeclaringClass() {
        return declaringClass;
    }

    public String getTypeVariable() {
        return typeVariable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GenericType)) return false;
        GenericType that = (GenericType) o;
        return declaringClass.equals(that.declaringClass) && typeVariable.equals(that.typeVariable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaringClass, typeVariable);
    }

    @Override
    public String toString() {
        return "[" + declaringClass.getSimpleName() + "." + typeVariable + "]";
    }
}
