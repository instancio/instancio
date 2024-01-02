/*
 * Copyright 2022-2024 the original author or authors.
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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies the seed for the random number generator.
 * <p>
 * Example:
 * <pre class="code"><code class="java">
 *
 * &#064;ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *
 *     &#064;Test
 *     <b>&#064;Seed(12345)</b>
 *     void someTestMethod() {
 *         Person person = Instancio.create(Person.class); // will use the specified seed
 *     }
 * }
 * </code></pre>
 * <p>
 * If the {@link Seed} annotation is specified, the same data set will be generated each test run
 * based on the given seed value.
 * <p>
 * If the seed annotation is not specified, a random seed will be used, resulting
 * in random data generated on each test run.
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Seed {

    /**
     * Specifies seed value for the Random Number Generator.
     *
     * @return seed value
     */
    long value();
}
