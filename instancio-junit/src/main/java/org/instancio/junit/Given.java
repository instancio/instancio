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
 * @see GivenProvider
 * @since 5.0.0
 */
@ExperimentalApi
@Documented
@Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
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
}
