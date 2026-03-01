/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.junit;

import org.instancio.Random;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.exception.InstancioApiException;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

/**
 * An interface for providing instances of objects
 * that can be injected into fields or parameters using
 * the {@link Given @Given} annotation.
 *
 * <p>For example, we may want to define a custom annotation
 * for generating numeric strings:
 *
 * <pre>{@code
 * @ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *
 *     @Test
 *     void example(@NumericString(length = 5) String digits) {
 *         // Possible value of digits: 04194
 *     }
 * }
 * }</pre>
 *
 * <p>To support the above use case, first we define the
 * {@code @NumericString} annotation:
 *
 * <pre>{@code
 * @Given(NumericStringProvider.class)
 * @Target({ElementType.FIELD, ElementType.PARAMETER})
 * @Retention(RetentionPolicy.RUNTIME)
 * @interface NumericString {
 *     int length();
 * }
 * }</pre>
 *
 * <p>where the {@code NumericStringProvider} can be implemented as:
 *
 * <pre>{@code
 * class NumericStringProvider implements GivenProvider {
 *     @Override
 *     public Object provide(ElementContext context) {
 *         NumericString numericString = context.getAnnotation(NumericString.class);
 *         return context.random().digits(numericString.length());
 *     }
 * }
 * }</pre>
 *
 * @see Given
 * @since 5.0.0
 */
@ExperimentalApi
public interface GivenProvider {

    /**
     * Provides an instance of an object based on the given context.
     *
     * @param context the context containing metadata about the element
     *                (either a {@link Field} or {@link Parameter}) annotated
     *                or meta-annotated with the {@link Given @Given} annotation
     * @return an instance of an object
     * @since 5.0.0
     */
    @ExperimentalApi
    Object provide(ElementContext context);

    /**
     * An interface representing the context of an element
     * (either a {@link Field} or {@link Parameter})
     * for which an object instance is being provided.
     */
    @ExperimentalApi
    interface ElementContext {

        /**
         * Returns the random instance used by Instancio to generate data.
         *
         * <p>Using this instance ensures that data will be reproducible.
         *
         * @return the random instance
         * @since 5.0.0
         */
        @ExperimentalApi
        Random random();

        /**
         * Returns the annotated element
         * (either a {@link Field} or {@link Parameter})
         * for which an object instance is being provided.
         *
         * @return the annotated element
         * @since 5.0.0
         */
        @ExperimentalApi
        AnnotatedElement getTargetElement();

        /**
         * Returns the type of the element for which
         * an instance is being provided. The type is:
         *
         * <ul>
         *   <li>{@link Field#getGenericType()} for fields</li>
         *   <li>{@link Parameter#getParameterizedType()} for parameters</li>
         * </ul>
         *
         * @return the type of object to be provided
         * @see #getTargetClass()
         * @since 5.0.0
         */
        @ExperimentalApi
        Type getTargetType();

        /**
         * Returns the class of the element for which
         * an instance is being provided.
         *
         * @return the class of object to be provided
         * @see #getTargetType()
         * @since 5.0.0
         */
        @ExperimentalApi
        Class<?> getTargetClass();

        /**
         * Returns an annotation of the specified type, if present.
         *
         * @param annotationType the type of the annotation to retrieve
         * @return the annotation if present, otherwise {@code null}
         * @throws InstancioApiException is more than one annotation
         *                               of the specified type is found
         * @since 5.0.0
         */
        @NullUnmarked
        @ExperimentalApi
        <A extends @Nullable Annotation> A getAnnotation(Class<A> annotationType);
    }
}
