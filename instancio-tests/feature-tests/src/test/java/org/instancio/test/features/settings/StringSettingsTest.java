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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.settings.StringCase;
import org.instancio.settings.StringType;
import org.instancio.test.support.asserts.StringAssertExtras;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag(Feature.SETTINGS)
@ExtendWith(InstancioExtension.class)
class StringSettingsTest {

    private static final int MIN_SIZE_OVERRIDE = 100;
    private static final int MAX_SIZE_OVERRIDE = 102;

    private static final Settings settings = Settings.create()
            .set(Keys.STRING_MIN_LENGTH, MIN_SIZE_OVERRIDE)
            .set(Keys.STRING_MAX_LENGTH, MAX_SIZE_OVERRIDE)
            // increase collection size for bigger sample
            .set(Keys.COLLECTION_MIN_SIZE, MIN_SIZE_OVERRIDE)
            .set(Keys.COLLECTION_MAX_SIZE, MAX_SIZE_OVERRIDE)
            .lock();

    @Test
    @DisplayName("Override default string length range")
    void length() {
        final StringHolder result = Instancio.of(StringHolder.class).withSettings(settings).create();
        assertThat(result.getValue()).hasSizeBetween(MIN_SIZE_OVERRIDE, MAX_SIZE_OVERRIDE);
    }

    @Test
    @DisplayName("Override allowEmpty to true")
    void allowEmpty() {
        final Settings overrides = settings.merge(Settings.create().set(Keys.STRING_ALLOW_EMPTY, true));
        final ListString result = Instancio.of(ListString.class).withSettings(overrides).create();
        assertThat(result.getList()).contains("");
    }

    @Test
    @DisplayName("Override nullable to true - generates null in String fields")
    void nullable() {
        final Settings overrides = settings.merge(Settings.create().set(Keys.STRING_NULLABLE, true));
        final Set<String> results = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            final StringHolder result = Instancio.of(StringHolder.class).withSettings(overrides).create();
            results.add(result.getValue());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Override nullable to true - does not generate null in collection elements")
    void stringIsNotNullInCollections() {
        final Settings overrides = settings.merge(Settings.create().set(Keys.STRING_NULLABLE, true));
        final ListString result = Instancio.of(ListString.class).withSettings(overrides).create();
        assertThat(result.getList()).doesNotContainNull();
    }

    @Test
    void unicode() {
        final int expectedSize = 1000;
        final String result = Instancio.of(String.class)
                .withSetting(Keys.STRING_TYPE, StringType.UNICODE)
                .generate(allStrings(), gen -> gen.string().length(expectedSize))
                .create();

        StringAssertExtras.assertString(result)
                .hasUnicodeBlockCountGreaterThan(10)
                .hasCodePointCount(expectedSize);
    }

    @Nested
    class StringCaseTest {

        private String createString(StringType stringType, StringCase stringCase) {
            return Instancio.of(String.class)
                    .withSetting(Keys.STRING_TYPE, stringType)
                    .withSetting(Keys.STRING_CASE, stringCase)
                    .create();
        }

        @Nested
        class AlphabeticTest {
            @Test
            void lowercase() {
                assertThat(createString(StringType.ALPHABETIC, StringCase.LOWER)).matches("[a-z]+");
            }

            @Test
            void mixedCase() {
                assertThat(createString(StringType.ALPHABETIC, StringCase.MIXED)).matches("[a-zA-Z]+");
            }

            @Test
            void uppercase() {
                assertThat(createString(StringType.ALPHABETIC, StringCase.UPPER)).matches("[A-Z]+");
            }
        }

        @Nested
        class HexTest {
            @Test
            void lowercase() {
                assertThat(createString(StringType.HEX, StringCase.LOWER)).matches("[0-9a-f]+");
            }

            @Test
            void mixedCase() {
                assertThat(createString(StringType.HEX, StringCase.MIXED)).matches("[0-9a-fA-F]+");
            }

            @Test
            void uppercase() {
                assertThat(createString(StringType.HEX, StringCase.UPPER)).matches("[0-9A-F]+");
            }
        }

        @Nested
        class AlphanumericTest {
            @Test
            void lowercase() {
                assertThat(createString(StringType.ALPHANUMERIC, StringCase.LOWER)).matches("[0-9a-z]+");
            }

            @Test
            void mixedCase() {
                assertThat(createString(StringType.ALPHANUMERIC, StringCase.MIXED)).matches("[0-9a-zA-Z]+");
            }

            @Test
            void uppercase() {
                assertThat(createString(StringType.ALPHANUMERIC, StringCase.UPPER)).matches("[0-9A-Z]+");
            }
        }

        @EnumSource(StringCase.class)
        @ParameterizedTest
        void caseShouldBeIgnoredForDigits(final StringCase stringCase) {
            assertThat(createString(StringType.DIGITS, stringCase)).matches("[0-9]+");
        }

        @EnumSource(StringCase.class)
        @ParameterizedTest
        void caseShouldBeIgnoredForNumericSequence(final StringCase stringCase) {
            assertThat(createString(StringType.NUMERIC_SEQUENCE, stringCase)).isEqualTo("1");
        }

        @EnumSource(StringCase.class)
        @ParameterizedTest
        void caseShouldBeIgnoredForUnicode(final StringCase stringCase) {
            assertThat(createString(StringType.UNICODE, stringCase)).isNotEmpty();
        }
    }
}
