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
package org.instancio.junit;

import org.instancio.junit.internal.InstancioArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides arguments for {@code @ParameterizedTest} methods.
 * Supports multiple arguments. Each argument will be a fully-populated instance.
 * <p>
 * Example:
 * <pre class="code"><code class="java">
 *
 * &#064;ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *
 *     &#064;ParameterizedTest
 *     <b>&#064;InstancioSource</b>
 *     void someTestMethod(Person person) {
 *         // ... use supplied person
 *     }
 * }
 * </code></pre>
 */
@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ArgumentsSource(InstancioArgumentsProvider.class)
public @interface InstancioSource {
}