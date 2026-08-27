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

import org.instancio.documentation.ExperimentalApi;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a field or parameter should be automatically
 * generated with a random value during test execution.
 *
 * <p>This annotation can be applied to:
 *
 * <ul>
 *   <li>fields</li>
 *   <li>{@code @Test} method parameters</li>
 *   <li>{@code @RepeatedTest} method parameters</li>
 *   <li>additional {@code @ParameterizedTest} method parameters</li>
 * </ul>
 *
 * <pre>{@code
 * @ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *
 *     @Given
 *     private List<Person> persons;
 *
 *     @Test
 *     void example1(@Given int randomInt) {
 *         // Regular @Test method with generated parameters
 *     }
 *
 *     @RepeatedTest(10)
 *     void example2(@Given String randomString) {
 *         // @RepeatedTest method with generated parameters
 *     }
 *
 *     @ValueSource(strings = {"foo", "bar", "baz"})
 *     @ParameterizedTest
 *     void example3(String value, @Given LocalDate randomDate) {
 *         // Supplement @ParameterizedTest arguments with additional generated arguments
 *         // Augment @ParameterizedTest arguments with additional generated values
 *     }
 * }
 * }</pre>
 *
 * <h2>Repeated declarations</h2>
 *
 * <p>This annotation is repeatable. Repeating it is equivalent to listing
 * all the providers in a single declaration, so that:
 *
 * <pre>{@code
 * @Given(FooProvider.class)
 * @Given(BarProvider.class)
 * private String value;
 * }</pre>
 *
 * <p>has the same effect as:
 *
 * <pre>{@code
 * @Given( {FooProvider.class, BarProvider.class} )
 * private String value;
 * }</pre>
 *
 * <p>Note that repeating the annotation adds an alternative rather than an
 * additional step: the providers form a single set of candidates, from which
 * one is selected at random each time a value is generated. They are never
 * applied together.
 *
 * <p>Candidates are gathered from every {@code @Given} that applies to an
 * element, whether declared directly or contributed by a meta-annotation,
 * so that composed annotations combine into one set.
 *
 * @see GivenProvider
 * @since 5.0.0
 */
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(Given.List.class)
public @interface Given {

    /**
     * Specifies classes that will provide values for
     * the annotated parameter or field.
     *
     * <ul>
     *   <li>If no class is specified, Instancio will generate
     *       random values for elements annotated with {@code @Given}.</li>
     *   <li>If multiple classes are specified, a provider will be
     *       selected randomly from the specified classes.</li>
     * </ul>
     *
     * @return the classes responsible for providing values
     * @since 5.0.0
     */
    @ExperimentalApi
    Class<? extends GivenProvider>[] value() default {};

    /**
     * Container for repeated {@link Given @Given} declarations.
     *
     * <p>This annotation is applied automatically by the compiler and is not
     * intended to be declared directly.
     *
     * @since 6.0.0
     */
    @ExperimentalApi
    @Documented
    @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {

        /**
         * The contained {@link Given @Given} annotations.
         *
         * @return the repeated annotations
         * @since 6.0.0
         */
        Given[] value();
    }
}
