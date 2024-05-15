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

package org.instancio.test.features.generator.string;

import org.instancio.Instancio;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.asserts.StringAssertExtras;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.Character.UnicodeBlock;
import java.math.BigInteger;
import java.util.Set;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATE, Feature.STRING_GENERATOR})
@ExtendWith(InstancioExtension.class)
class StringGeneratorTest {

    private static final int STRING_LENGTH = 200;
    private static final String DIGIT_CHARS = "[0-9]+";
    private static final String LCASE_CHARS = "[a-z]+";
    private static final String UCASE_CHARS = "[A-Z]+";

    @Test
    void disallowEmpty() {
        final int sampleSize = 500;
        final Set<String> result = Instancio.ofSet(String.class)
                .size(sampleSize)
                .withSettings(Settings.create().set(Keys.STRING_ALLOW_EMPTY, true))
                .generate(allStrings(), gen -> gen.string().allowEmpty(false))
                .create();

        assertThat(result)
                .hasSize(sampleSize)
                .doesNotContain("");
    }

    @Test
    void suffix() {
        final int length = 5;
        final String suffix = "-foo";
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.string().length(length).suffix(suffix))
                .create();

        assertThat(result)
                .hasSize(length + suffix.length())
                .endsWith(suffix);
    }

    @Nested
    class StringTypesTest {
        @Test
        void precondition() {
            // tests require a long string to ensure all expected character types are generated
            assertThat(create(StringGeneratorSpec::upperCase)).hasSize(STRING_LENGTH);
        }

        @Test
        void defaultCase() {
            assertThat(create(s -> s))
                    .isUpperCase().doesNotContainPattern(DIGIT_CHARS);
        }

        @Test
        void upperCase() {
            assertThat(create(StringGeneratorSpec::upperCase))
                    .isUpperCase().doesNotContainPattern(DIGIT_CHARS);
        }

        @Test
        void lowerCase() {
            assertThat(create(StringGeneratorSpec::lowerCase))
                    .isLowerCase().doesNotContainPattern(DIGIT_CHARS);
        }

        @Test
        void mixedCase() {
            assertThat(create(StringGeneratorSpec::mixedCase))
                    .isMixedCase().doesNotContainPattern(DIGIT_CHARS);
        }

        @Test
        void digits() {
            assertThat(create(StringGeneratorSpec::digits)).containsOnlyDigits();
        }

        @Nested
        class AlphaNumericTest {

            @Test
            void defaultCase() {
                assertThat(create(StringGeneratorSpec::alphaNumeric))
                        .isUpperCase()
                        .containsPattern(UCASE_CHARS)
                        .containsPattern(DIGIT_CHARS);
            }

            @Test
            void upperCase() {
                assertThat(create(s -> s.alphaNumeric().upperCase()))
                        .isUpperCase().containsPattern(DIGIT_CHARS);
            }

            @Test
            void lowerCase() {
                assertThat(create(s -> s.alphaNumeric().lowerCase()))
                        .isLowerCase()
                        .containsPattern(LCASE_CHARS)
                        .containsPattern(DIGIT_CHARS);
            }

            @Test
            void mixedCase() {
                assertThat(create(s -> s.alphaNumeric().mixedCase()))
                        .isMixedCase()
                        .containsPattern(LCASE_CHARS)
                        .containsPattern(UCASE_CHARS)
                        .containsPattern(DIGIT_CHARS);
            }
        }

        @Nested
        class HexTest {

            @Test
            void defaultCase() {
                final String result = create(StringGeneratorSpec::hex);
                assertThat(new BigInteger(result, 16)).isNotNull();

                assertThat(result)
                        .isUpperCase()
                        .containsPattern(UCASE_CHARS)
                        .containsPattern(DIGIT_CHARS);
            }

            @Test
            void upperCase() {
                final String result = create(s -> s.hex().upperCase());
                assertThat(new BigInteger(result, 16)).isNotNull();
                assertThat(result).isUpperCase();
            }

            @Test
            void lowerCase() {
                final String result = create(s -> s.hex().lowerCase());
                assertThat(new BigInteger(result, 16)).isNotNull();

                assertThat(result)
                        .isLowerCase()
                        .containsPattern(LCASE_CHARS)
                        .containsPattern(DIGIT_CHARS);
            }

            @Test
            void mixedCase() {
                final String result = create(s -> s.hex().mixedCase());
                assertThat(new BigInteger(result, 16)).isNotNull();

                assertThat(result)
                        .isMixedCase()
                        .containsPattern(LCASE_CHARS)
                        .containsPattern(UCASE_CHARS)
                        .containsPattern(DIGIT_CHARS);
            }
        }

        @Nested
        class UnicodeTest {
            @Test
            void unicode() {
                final int length = 100_000;
                final String result = create(s -> s.unicode().length(length));

                StringAssertExtras.assertString(result)
                        //Since we're generating a large string, we should have at least 150 blocks
                        .hasUnicodeBlockCountGreaterThan(150)
                        .hasCodePointCount(length);
            }

            @Test
            void unicodeBlocks() {
                final int length = 100;
                final String result = create(s -> s.unicode(UnicodeBlock.CYRILLIC, UnicodeBlock.EMOTICONS).length(length));

                StringAssertExtras.assertString(result)
                        .hasUnicodeBlockCount(2)
                        .hasCodePointCount(length)
                        .hasCodePointsFrom(UnicodeBlock.CYRILLIC, UnicodeBlock.EMOTICONS);
            }

            @Test
            void lengthZero() {
                assertThat(create(s -> s.unicode().length(0))).isEmpty();
            }
        }
    }

    private String create(final Function<StringGeneratorSpec, StringGeneratorSpec> fn) {
        final StringHolder result = Instancio.of(StringHolder.class)
                .generate(allStrings(), gen -> fn.apply(gen.string().length(STRING_LENGTH)))
                .create();

        return result.getValue();
    }

}