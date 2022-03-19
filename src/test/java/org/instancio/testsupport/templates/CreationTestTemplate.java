package org.instancio.testsupport.templates;

import org.instancio.Instancio;
import org.instancio.TypeTokenSupplier;
import org.instancio.testsupport.tags.CreateTag;
import org.instancio.testsupport.utils.TypeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.ReflectionAssert.assertThatObject;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * Base test class for verifying objects generated via the API.
 * <p>
 * Supports manual verification via the {@link  #verify(Object)} method.
 * <p>
 * Will also auto-verify via reflections that the entire object graph is populated:
 * that is, it does not contain {@code null} values, empty collections, maps, or arrays.
 * <p>
 * If auto-verification is not desired, for example in case of
 * <ul>
 *   <li>nullable fields</li>
 *   <li>cyclic graphs where {@code null} is used to end the cycle</li>
 * </ul>
 * <p>
 * then the {@link #verify(Object)} method can be annotated with {@link AutoVerificationDisabled}.
 *
 * @param <T> type being verified
 */
@CreateTag
@TestInstance(PER_CLASS)
public abstract class CreationTestTemplate<T> {

    private Type genericType;
    private Class<?> typeClass;

    @BeforeAll
    protected final void templateSetup() {
        TypeContext typeContext = new TypeContext(this.getClass());
        genericType = typeContext.getGenericType();
        typeClass = typeContext.getTypeClass();
    }

    /**
     * Kicks off the test method.
     */
    @SuppressWarnings("unchecked")
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
        final String displayName = "of type " + TypeUtils.shortenPackageNames(genericType.getTypeName());
        final Arguments[] arguments = new Arguments[numberOfExecutions()];

        for (int i = 0; i < arguments.length; i++) {
            TypeTokenSupplier<Type> typeSupplier = () -> genericType;
            Object result = Instancio.of(typeSupplier).create();
            arguments[i] = Arguments.of(Named.of(displayName, result));
        }

        return Stream.of(arguments);
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

    private <A extends Annotation> Optional<A> getVerifyMethodAnnotation(Class<?> methodArgType, Class<A> annotationClass) {
        try {
            final Method verifyMethod = getClass().getDeclaredMethod("verify", methodArgType);
            return Optional.ofNullable(verifyMethod.getAnnotation(annotationClass));
        } catch (Exception ex) {
            throw new AssertionError(String.format("'verify(%s)' method not found", methodArgType.getSimpleName()), ex);
        }
    }

}
