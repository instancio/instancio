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

import org.instancio.settings.Settings;

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
 * <pre>{@code
 * @ExtendWith(InstancioExtension.class)
 * class ExampleTest {
 *     @WithSettings
 *     private final Settings settings = Settings.create()
 *         .set(Keys.COLLECTION_MIN_SIZE, 50)
 *         .set(Keys.COLLECTION_MAX_SIZE, 100);
 *
 *     @Test
 *     void example() {
 *
 *     }
 * }
 * }</pre>
 *
 * <h2>{@code @Nested} Test Classes</h2>
 *
 * <p>Prior to version {@code 6.0.0}, settings defined in {@code @Nested}
 * test classes were independent from those in their outer classes.
 *
 * <p>Since version {@code 6.0.0}, when {@code @WithSettings} is used in
 * {@code @Nested} test classes, the inner class settings are overlaid on
 * top of the outer class settings. In other words, settings defined in the
 * inner class override any matching settings from the outer class, for example:
 *
 * <pre>{@code
 * @WithSettings
 * private final Settings outer = Settings.create()
 *     .set(Keys.STRING_MIN_LENGTH, 3)
 *     .set(Keys.STRING_MAX_LENGTH, 20);
 *
 * @Nested
 * class InnerTest {
 *     @WithSettings
 *     private final Settings inner = Settings.create()
 *         .set(Keys.STRING_MIN_LENGTH, 10);
 *
 *     // Effective value of STRING_MIN_LENGTH = 10 and STRING_MAX_LENGTH = 20
 * }
 * }</pre>
 *
 * @since 1.1.1
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WithSettings {
}
