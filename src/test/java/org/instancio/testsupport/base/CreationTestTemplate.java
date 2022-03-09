package org.instancio.testsupport.base;

import org.instancio.Instancio;
import org.instancio.testsupport.asserts.ReflectionAssert;
import org.instancio.testsupport.tags.CreateTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Base test class for verifying objects generated via the API.
 * <p>
 * Supports manual verification via the {@link  #verify(Object)} method.
 * <p>
 * <li>Will also auto-verify via reflections that the entire object graph is populated:
 * that is, it does not contain {@code null} values, empty collections, maps, or arrays.
 * If auto-verification is not desired (e.g. with nullable fields) then the
 * {@link #verify(Object)} method can be annotated with {@link AutoVerificationDisabled}.
 *
 * @param <T> type being verified
 */
@CreateTag
@TestInstance(PER_CLASS)
public abstract class CreationTestTemplate<T> {

    private Class<?> typeClass;
    private Class<?>[] typeArguments;

    @BeforeAll
    protected void creationTemplateSetup() {
        final Class<?> klass = this.getClass();
        final ParameterizedType genericSuperclass = (ParameterizedType) klass.getGenericSuperclass();
        final Type typeToCreate = genericSuperclass.getActualTypeArguments()[0];

        this.typeClass = typeToCreate instanceof ParameterizedType
                ? (Class<?>) ((ParameterizedType) typeToCreate).getRawType()
                : (Class<?>) typeToCreate;

        final Type[] actualTypeArgs = typeToCreate instanceof ParameterizedType
                ? ((ParameterizedType) typeToCreate).getActualTypeArguments()
                : new Type[0];

        this.typeArguments = Arrays.stream(actualTypeArgs)
                .map(it -> (Class<?>) it)
                .toArray(Class<?>[]::new);
    }

    /**
     * A method for verifying a created instance of type {@link T}.
     *
     * @param result created object to verify
     */
    protected abstract void verify(T result);

    /**
     * Kicks off the test method.
     */
    @MethodSource("generatedObjectSource")
    @ParameterizedTest(name = "{index}: {0}")
    protected final void verifyingGenerated(Object result) {
        verify((T) result); // manual verification

        if (isAutoVerificationEnabled()) {
            ReflectionAssert.assertThatObject(result)
                    .as("Auto-verification failed for object of class '%s':\n%s",
                            result.getClass().getName(), result)
                    .isFullyPopulated();
        }
    }

    private Stream<Arguments> generatedObjectSource() {
        final String displayName = getTestDisplayName();
        final Arguments[] arguments = new Arguments[numberOfExecutions()];

        for (int i = 0; i < arguments.length; i++) {
            T result = (T) Instancio.of(typeClass).withType(typeArguments).create();
            arguments[i] = Arguments.of(Named.of(displayName, result));
        }

        return Stream.of(arguments);
    }

    private String getTestDisplayName() {
        String displayName = "of type " + typeClass.getSimpleName();

        if (typeArguments.length > 0) {
            String types = Arrays.stream(typeArguments)
                    .map(Class::getSimpleName)
                    .collect(joining(","));

            displayName += "<" + types + ">";
        }
        return displayName;
    }

    private boolean isAutoVerificationEnabled() {
        return !getVerifyMethodAnnotation(typeClass, AutoVerificationDisabled.class).isPresent();
    }

    private int numberOfExecutions() {
        final Class<NumberOfExecutions> annotationClass = NumberOfExecutions.class;

        final int numExecutions = getVerifyMethodAnnotation(typeClass, annotationClass)
                .map(NumberOfExecutions::value)
                .orElse(1);

        assertThat(numExecutions)
                .withFailMessage("The '@%s(%s)' is invalid. Value must be greater than 0.",
                        annotationClass.getSimpleName(), numExecutions)
                .isPositive();

        return numExecutions;
    }

    private <A extends Annotation> Optional<A> getVerifyMethodAnnotation(Class<?> rawClass, Class<A> annotationClass) {
        try {
            final Method verifyMethod = getClass().getDeclaredMethod("verify", rawClass);
            return Optional.ofNullable(verifyMethod.getAnnotation(annotationClass));
        } catch (Exception ex) {
            throw new AssertionError("Could not get number of executions", ex);
        }
    }


}
