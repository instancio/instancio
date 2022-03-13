package org.instancio.testsupport.templates;

import org.instancio.Instancio;
import org.instancio.testsupport.tags.CreateTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.ReflectionAssert.assertThatObject;
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
    protected final void templateSetup() {
        TypeContext typeContext = new TypeContext(this.getClass());
        typeClass = typeContext.getTypeClass();
        typeArguments = typeContext.getTypeArguments();
    }

    /**
     * Kicks off the test method.
     */
    @MethodSource("instancioCreatedObjects")
    @ParameterizedTest(name = "{index}: {0}")
    protected final void verifyingGenerated(Object result) {
        assertThat(result).isNotNull();

        verify((T) result); // manual verification

        if (isAutoVerificationEnabled()) {
            assertThatObject(result)
                    .as("Auto-verification failed for object of class '%s':\n%s",
                            result.getClass().getName(), result)
                    .isFullyPopulated();
        }
    }

    /**
     * A method for verifying a created instance of type {@link T}.
     *
     * @param result created object to verify
     */
    protected abstract void verify(T result);

    private Stream<Arguments> instancioCreatedObjects() {
        final String displayName = getTestDisplayName();
        final Arguments[] arguments = new Arguments[numberOfExecutions()];

        for (int i = 0; i < arguments.length; i++) {
            Object result = Instancio.of(typeClass)
                    .withType(typeArguments)
                    .create();

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
