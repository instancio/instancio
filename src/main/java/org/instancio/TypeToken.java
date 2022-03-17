package org.instancio;

import org.instancio.util.Verify;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A supplier that provides {@link Type} information. It can be used
 * with generic classes in order to avoid "unchecked assignment" warnings.
 * <p>
 * The following examples both create an instance of <tt>Pair</tt> class.
 *
 * <pre>{@code
 *      // Generates an "unchecked assignment" warning
 *      // which can be suppressed with @SuppressWarnings("unchecked")
 *      Pair<Integer, String> pair = Instancio.of(Pair.class)
 *          .withTypeParameters(Integer.class, String.class)
 *          .create();
 *
 *     // This usage avoids the warning
 *     Pair<Integer, String> pair = Instancio.of(new TypeToken<Pair<Integer, String>>() {}).create();
 * }</pre>
 *
 * @param <T> type being supplied.<p>
 *            Required to be present, though not used directly.
 */
public interface TypeToken<T> extends TypeTokenSupplier<T> {

    /**
     * Returns the type to be created.
     *
     * @return type to create
     */
    @Override
    default Type get() {
        Type superClass = getClass().getGenericInterfaces()[0];
        Verify.isTrue(superClass instanceof ParameterizedType, "Missing type parameter");
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

}