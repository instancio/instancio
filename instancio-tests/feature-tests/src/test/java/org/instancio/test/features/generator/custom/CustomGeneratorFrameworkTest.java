/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATOR, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class CustomGeneratorFrameworkTest {

    private static final int STRING_LENGTH = 100;

    private static class StringHolderDigitsGenerator implements Generator<StringHolder> {
        private GeneratorContext context;
        int minLength;
        int maxLength;
        int initInvocationCount;

        @Override
        public void init(final GeneratorContext context) {
            this.context = context;
            this.minLength = context.getSettings().get(Keys.STRING_MIN_LENGTH);
            this.maxLength = context.getSettings().get(Keys.STRING_MAX_LENGTH);
            initInvocationCount++;
        }

        @Override
        public StringHolder generate(final Random random) {
            assertThat(random).isSameAs(context.random());
            final int length = random.intRange(minLength, maxLength);
            return new StringHolder(random.digits(length));
        }
    }

    @Test
    @DisplayName("Customised settings are propagated to generator via generator context")
    void generatorWithCustomisedSettingsViaContext() {
        final StringHolderDigitsGenerator generator = new StringHolderDigitsGenerator();
        final StringHolder result = Instancio.of(StringHolder.class)
                .supply(all(StringHolder.class), generator)
                .withSettings(Settings.create()
                        .set(Keys.STRING_MIN_LENGTH, STRING_LENGTH)
                        .set(Keys.STRING_MAX_LENGTH, STRING_LENGTH))
                .create();

        // The settings provided by GeneratorContext should reflect
        // the custom settings passed via withSettings() method
        assertThat(generator.minLength).isEqualTo(generator.maxLength)
                .isEqualTo(STRING_LENGTH);

        assertThat(result.getValue())
                .hasSize(STRING_LENGTH)
                .containsOnlyDigits();
    }

    @Test
    @DisplayName("init() should be called each time prior to generate()")
    void initShouldBeCalledPriorToGenerate() {
        final int numberOfResults = 10;
        final StringHolderDigitsGenerator generator = new StringHolderDigitsGenerator();

        final Stream<String> results = Instancio.of(StringHolder.class)
                .supply(all(StringHolder.class), generator)
                .stream()
                .limit(numberOfResults)
                .map(StringHolder::getValue);

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            // without custom settings, context should provide default settings
            assertThat(result)
                    .hasSizeLessThan(STRING_LENGTH)
                    .isUpperCase();
        });

        assertThat(generator.initInvocationCount)
                .as("Generator should be initialised exactly once")
                .isOne();
    }

    @Test
    @DisplayName("Settings provided by GeneratorContext should be locked")
    void settingsShouldBeLocked() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .supply(all(StringHolder.class), new StringHolderDigitsGenerator() {
                    @Override
                    public void init(final GeneratorContext context) {
                        context.getSettings().set(Keys.STRING_NULLABLE, true);
                    }
                });

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnsupportedOperationException.class)
                .hasMessage("This instance of Settings has been locked and is read-only");
    }
}
