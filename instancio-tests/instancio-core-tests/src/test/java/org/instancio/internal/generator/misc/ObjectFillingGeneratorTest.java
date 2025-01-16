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
package org.instancio.internal.generator.misc;

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.FillType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class ObjectFillingGeneratorTest {

    private final Settings settings = Settings.defaults();
    private final Random random = new DefaultRandom();

    private static Stream<Arguments> expectedMapping() {
        return Stream.of(
                Arguments.of(FillType.APPLY_SELECTORS, AfterGenerate.APPLY_SELECTORS),
                Arguments.of(FillType.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES, AfterGenerate.POPULATE_NULLS_AND_DEFAULT_PRIMITIVES),
                Arguments.of(FillType.POPULATE_NULLS, AfterGenerate.POPULATE_NULLS));
    }

    @MethodSource("expectedMapping")
    @ParameterizedTest
    void fillTypeMapping(
            final FillType fillType,
            final AfterGenerate expectedAfterGenerate) {

        final GeneratorContext generatorContext = new GeneratorContext(settings, random);
        final ObjectFillingGenerator generator = new ObjectFillingGenerator(
                generatorContext, new Object(), fillType);

        final AfterGenerate afterGenerate = generator.hints().afterGenerate();

        assertThat(afterGenerate).isEqualTo(expectedAfterGenerate);
    }

    @MethodSource("expectedMapping")
    @ParameterizedTest
    void whenFillTypeIsNull_itShouldDefaultToSettingsValue(
            final FillType fillTypeFromSettings,
            final AfterGenerate expectedAfterGenerate) {

        settings.set(Keys.FILL_TYPE, fillTypeFromSettings);
        final GeneratorContext generatorContext = new GeneratorContext(settings, random);

        final ObjectFillingGenerator generator = new ObjectFillingGenerator(
                generatorContext, new Object(), /* fillType = */ null);

        final AfterGenerate afterGenerate = generator.hints().afterGenerate();

        assertThat(afterGenerate).isEqualTo(expectedAfterGenerate);
    }
}
