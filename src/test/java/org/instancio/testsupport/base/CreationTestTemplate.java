package org.instancio.testsupport.base;

import org.instancio.Instancio;
import org.instancio.testsupport.asserts.ReflectionAssert;
import org.instancio.testsupport.tags.CreateTag;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Base test class for verifying objects generated via the API.
 *
 * <p>
 * Supports manual verification via the {@link  #verify(Object)} method.
 * <p>
 * <li>Will also auto-verify via reflections the entire object graph is populated
 * if {@link #autoVerify()} returns {@code true}. If this is not desired, disable
 * auto-verification by overriding the method to return {@code false}.
 *
 * @param <T>
 */
@CreateTag
@TestInstance(PER_CLASS)
public abstract class CreationTestTemplate<T> {

    /**
     * A method for verifying a created instance of type {@link T}.
     *
     * @param result created object to verify
     */
    protected abstract void verify(T result);

    /**
     * Indicates whether to auto-verify generated object via reflection.
     *
     * @return if {@code true}, auto-verification will be performed.
     */
    protected boolean autoVerify() {
        return true;
    }

    /**
     * Kicks off the main test method.
     */
    @MethodSource("generatedObjectSource")
    @ParameterizedTest(name = "{0}")
    protected final void verifyingGenerated(Object result) {
        verify((T) result); // manual verification

        if (autoVerify()) {
            ReflectionAssert.assertThatObject(result)
                    .as("Auto-verification failed for object of class '%s':\n%s",
                            result.getClass().getName(), result)
                    .isFullyPopulated();
        }
    }

    private Stream<Arguments> generatedObjectSource() {
        final Class<?> klass = this.getClass();
        final ParameterizedType genericSuperclass = (ParameterizedType) klass.getGenericSuperclass();
        final Type typeToCreate = genericSuperclass.getActualTypeArguments()[0];

        final Class<?> rawClass;
        if (typeToCreate instanceof ParameterizedType) {
            rawClass = (Class<?>) ((ParameterizedType) typeToCreate).getRawType();
        } else {
            rawClass = (Class<?>) typeToCreate;
        }

        Type[] actualTypeArgs = new Type[0];

        if (typeToCreate instanceof ParameterizedType) {
            actualTypeArgs = ((ParameterizedType) typeToCreate).getActualTypeArguments();
        }

        final Class<?>[] typeArgs = Arrays.stream(actualTypeArgs)
                .map(it -> (Class<?>) it)
                .toArray(Class<?>[]::new);

        final T result = (T) Instancio.of(rawClass).withType(typeArgs).create();
        final String displayName = getTestDisplayName(rawClass, typeArgs);

        return Stream.of(Arguments.of(Named.of(displayName, result)));
    }

    private static String getTestDisplayName(Class<?> rawClass, Class<?>[] typeArgs) {
        String displayName = "of type " + rawClass.getSimpleName();

        if (typeArgs.length > 0) {
            String types = Arrays.stream(typeArgs)
                    .map(Class::getSimpleName)
                    .collect(joining(","));

            displayName += "<" + types + ">";
        }
        return displayName;
    }
}
