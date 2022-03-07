package experimental.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public abstract class TypeReference<T> {

    private final Type type;
    private volatile Constructor<?> constructor;

    protected TypeReference() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    /**
     * Instantiates a new instance of {@code T} using the default, no-arg
     * constructor.
     */
    @SuppressWarnings("unchecked")
    public T newInstance()
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, InstantiationException {
        if (constructor == null) {
            Class<?> rawType = type instanceof Class<?>
                    ? (Class<?>) type
                    : (Class<?>) ((ParameterizedType) type).getRawType();
            constructor = rawType.getConstructor();
        }
        return (T) constructor.newInstance();
    }

    /**
     * Gets the referenced type.
     */
    public Type getType() {
        return this.type;
    }

    public static <T> T create(TypeReference<T> ref) throws Exception {
        return ref.newInstance();
    }

    public static void main(String[] args) throws Exception {
        List<String> l1 = new TypeReference<ArrayList<String>>() {
        }.newInstance();

        List<String> l2 = create(new TypeReference<ArrayList<String>>() {
        });

        l1.add("test l1");
        l2.add("test l2");

        System.out.println(l1);
        System.out.println(l2);

        // this will  fail since List is an interface (no constructor)
        List<String> l3 = create(new TypeReference<List<String>>() {
        });
        l3.add("test l3");
        System.out.println(l3);
    }
}