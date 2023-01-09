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
package org.instancio.test.features.generator.string;

import org.instancio.Instancio;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.Asserts.assertDoesNotHaveFieldPrefix;
import static org.instancio.test.support.asserts.Asserts.assertHasFieldPrefix;

@FeatureTag({
        Feature.GENERATE,
        Feature.STRING_GENERATOR,
        Feature.STRING_FIELD_PREFIX,
        Feature.SETTINGS
})
@ExtendWith(InstancioExtension.class)
class StringFieldPrefixEnabledViaSettingsTest {

    private static final int SAMPLE_SIZE = 200;
    private static final String FIELD_PREFIX = "value_";

    private static final Settings PREFIX_ENABLED = Settings.create()
            .set(Keys.STRING_FIELD_PREFIX_ENABLED, true)
            .lock();

    @Nested
    class ShouldNotBePrefixedTest {
        @WithSettings
        private final Settings settings = PREFIX_ENABLED;

        @Test
        void stringThatIsNotAField() {
            final String result = Instancio.create(String.class);
            assertDoesNotHaveFieldPrefix(FIELD_PREFIX, result);
        }

        @Test
        void stringThatIsACollectionElement() {
            final List<String> result = Instancio.ofList(String.class).create();
            assertThat(result).isNotEmpty()
                    .allSatisfy(str -> assertDoesNotHaveFieldPrefix(FIELD_PREFIX, str));
        }

        @Test
        void stringThatIsAnArrayElement() {
            final String[] result = Instancio.create(String[].class);
            assertThat(result).isNotEmpty()
                    .allSatisfy(str -> assertDoesNotHaveFieldPrefix(FIELD_PREFIX, str));
        }

        @Test
        void stringFromSet() {
            final StringHolder result = Instancio.of(StringHolder.class)
                    .set(field(StringHolder::getValue), "foo")
                    .create();

            assertThat(result.getValue()).isEqualTo("foo");
        }

        @Test
        void stringFromSupply() {
            final StringHolder result = Instancio.of(StringHolder.class)
                    .supply(field(StringHolder::getValue), () -> "foo")
                    .create();

            assertThat(result.getValue()).isEqualTo("foo");
        }

        @Test
        void stringFromGenerate() {
            final StringHolder result = Instancio.of(StringHolder.class)
                    .generate(field(StringHolder::getValue), Generators::string)
                    .create();

            assertDoesNotHaveFieldPrefix(FIELD_PREFIX, result.getValue());
        }
    }

    @Nested
    class ShouldBePrefixedTest {
        @WithSettings
        private final Settings settings = PREFIX_ENABLED;

        @Test
        void stringField() {
            final StringHolder result = Instancio.create(StringHolder.class);
            assertHasFieldPrefix(FIELD_PREFIX, result.getValue());
        }
    }

    @Nested
    class NullableAndEmptyStringTest {
        @WithSettings
        private final Settings settings = PREFIX_ENABLED;

        @Test
        @DisplayName("Empty string due to 'allow empty' setting should not be prefixed with field name")
        void allowEmptyViaSettings() {
            final Set<String> results = Instancio.of(StringHolder.class)
                    .withSettings(Settings.create().set(Keys.STRING_ALLOW_EMPTY, true))
                    .stream()
                    .limit(SAMPLE_SIZE)
                    .map(StringHolder::getValue)
                    .collect(Collectors.toSet());

            assertThat(results)
                    .contains("")
                    .doesNotContain(FIELD_PREFIX);

            assertThat(results.stream().filter(s -> !s.equals("")))
                    .isNotEmpty()
                    .allSatisfy(str -> assertHasFieldPrefix(FIELD_PREFIX, str));
        }

        @Test
        @DisplayName("Nullable string via 'Settings' should not be prefixed when null is generated")
        void nullableStringViaSettings() {
            final Set<String> results = Instancio.of(StringHolder.class)
                    .withSettings(Settings.create().set(Keys.STRING_NULLABLE, true))
                    .stream()
                    .limit(SAMPLE_SIZE)
                    .map(StringHolder::getValue)
                    .collect(Collectors.toSet());

            assertThat(results).containsNull();
            assertThat(results.stream().filter(Objects::nonNull))
                    .isNotEmpty()
                    .allSatisfy(str -> assertHasFieldPrefix(FIELD_PREFIX, str));
        }

        @Test
        @DisplayName("Nullable string via 'withNullable()' should not be prefixed when null is generated")
        void nullableStringViaWithNullable() {
            final Set<String> results = Instancio.of(StringHolder.class)
                    .withNullable(field(StringHolder::getValue))
                    .stream()
                    .limit(SAMPLE_SIZE)
                    .map(StringHolder::getValue)
                    .collect(Collectors.toSet());

            assertThat(results).containsNull();
            assertThat(results.stream().filter(Objects::nonNull))
                    .isNotEmpty()
                    .allSatisfy(str -> assertHasFieldPrefix(FIELD_PREFIX, str));
        }
    }
}
