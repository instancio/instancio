/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.settings.Settings;
import org.junit.jupiter.api.Nested;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for supplying custom settings to a unit test class.
 *
 * <p>This annotation must be placed on a {@link Settings} field.
 * There can be at most one field annotated {@code @WithSettings} per test class.
 *
 * <pre><code>
 * &#064;ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *     &#064;WithSettings
 *     private final Settings settings = Settings.create()
 *         .set(Keys.COLLECTION_MIN_SIZE, 50)
 *         .set(Keys.COLLECTION_MAX_SIZE, 100);
 *
 *     &#064;Test
 *     void example() {
 *
 *     }
 * }
 * </code></pre>
 *
 * <p><b>Note</b>: when a {@link Settings} instance is annotated with
 * {@code @WithSettings}, the specified settings are not automatically
 * applied to {@link Nested} test classes.
 *
 * @since 1.1.1
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithSettings {
}
