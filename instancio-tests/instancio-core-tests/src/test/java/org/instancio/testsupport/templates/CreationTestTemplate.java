/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.testsupport.templates;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeTokenSupplier;
import org.instancio.internal.util.Format;
import org.instancio.test.support.tags.CreateTag;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Named;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;
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

    private final TypeContext typeContext = new TypeContext(this.getClass());
    private Type genericType;
    private Class<?> typeClass;

    @BeforeAll
    protected final void templateSetup() {
        genericType = typeContext.getGenericType();
        typeClass = typeContext.getTypeClass();
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

    private Stream<Arguments> instancioCreatedObjects() {
        final int numExecutions = numberOfExecutions();
        final List<Arguments> arguments = new ArrayList<>();

        for (int i = 0; i < numExecutions; i++) {
            arguments.add(Arguments.of(Named.of(getDisplayName("type token"), createUsingTypeToken())));
            arguments.add(Arguments.of(Named.of(getDisplayName("model"), createUsingModel())));
        }

        return Stream.of(arguments.toArray(new Arguments[0]));
    }

    private Object createUsingModel() {
        final Model<?> model = Instancio.of((TypeTokenSupplier<Type>) () -> genericType).toModel();
        return Instancio.create(model);
    }

    private Object createUsingTypeToken() {
        return Instancio.create((TypeTokenSupplier<Type>) () -> genericType);
    }

    private String getDisplayName(final String apiMethod) {
        return String.format("of type '%s' using %s", Format.withoutPackage(genericType), apiMethod);
    }

    private boolean isAutoVerificationEnabled() {
        return getClass().getAnnotation(AutoVerificationDisabled.class) == null;
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
