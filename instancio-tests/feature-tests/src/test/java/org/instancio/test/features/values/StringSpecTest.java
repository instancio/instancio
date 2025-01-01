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
package org.instancio.test.features.values;

import org.instancio.Instancio;
import org.instancio.generator.specs.StringSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.Character.UnicodeBlock;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.asserts.StringAssertExtras.assertString;

@FeatureTag(Feature.VALUE_SPEC)
class StringSpecTest extends AbstractValueSpecTestTemplate<String> {

    @Override
    protected StringSpec spec() {
        return Instancio.gen().string();
    }

    @Test
    void prefix() {
        assertThat(spec().prefix("foo").get()).startsWith("foo");
    }

    @Test
    void suffix() {
        assertThat(spec().suffix("foo").get()).endsWith("foo");
    }

    @Test
    void length() {
        assertThat(spec().length(5).get()).hasSize(5);
        assertThat(spec().length(5, 7).get()).hasSizeBetween(5, 7);
        assertThat(spec().minLength(10).get()).hasSizeGreaterThanOrEqualTo(10);
        assertThat(spec().maxLength(1).get()).hasSizeLessThanOrEqualTo(1);
    }

    @Test
    void cases() {
        assertThat(spec().upperCase().get()).isUpperCase();
        assertThat(spec().lowerCase().get()).isLowerCase();
        assertThat(spec().mixedCase().length(20).get()).isMixedCase();
        assertThat(spec().alphaNumeric().length(20).get()).isAlphanumeric();
        assertThat(spec().digits().get()).containsOnlyDigits();
    }

    @Test
    void numericSequence() {
        assertThat(spec().numericSequence().list(3)).containsExactly("1", "2", "3");
    }

    @Test
    void unicode() {
        final int expectedSize = 1000;

        assertString(spec().unicode().length(expectedSize).get())
                .hasUnicodeBlockCountGreaterThan(10)
                .hasCodePointCount(expectedSize);
    }

    @Test
    void unicodeBlocks() {
        final int expectedSize = 1000;

        final String result = spec().length(expectedSize)
                .unicode(UnicodeBlock.CYRILLIC, UnicodeBlock.EMOTICONS, UnicodeBlock.GOTHIC)
                .get();

        assertString(result)
                .hasCodePointCount(expectedSize)
                .hasUnicodeBlockCount(3)
                .hasCodePointsFrom(UnicodeBlock.CYRILLIC, UnicodeBlock.EMOTICONS, UnicodeBlock.GOTHIC);
    }

    @Test
    void allowEmptyAndNullable() {
        final Stream<String> result = IntStream.range(0, 500)
                .mapToObj(i -> spec().allowEmpty().nullable().get());

        assertThat(result).contains("").containsNull();
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class DisallowEmptyTest {

        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.STRING_ALLOW_EMPTY, true);

        @Test
        void disallowEmpty() {
            final List<String> results = spec().allowEmpty(false).list(500);

            assertThat(results).hasSize(500).doesNotContain("");
        }
    }
}
