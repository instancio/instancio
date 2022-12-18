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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.Mode;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.root;

@FeatureTag({Feature.ROOT_SELECTOR, Feature.SELECTOR})
class RootSelectorTest {
    private static final String FOO = "foo";
    private static final String BAR = "bar";

    @Test
    void withCreateClass() {
        final String result = Instancio.of(String.class)
                .set(root(), FOO)
                .create();

        assertThat(result).isEqualTo(FOO);
    }

    @Test
    void withTypeToken() {
        final int rootListSize = 1;
        final int innerListSize = 3;

        final List<List<String>> result = Instancio.of(new TypeToken<List<List<String>>>() {})
                .generate(root(), gen -> gen.collection().size(rootListSize))
                .withSettings(Settings.create()
                        .set(Keys.COLLECTION_MIN_SIZE, innerListSize)
                        .set(Keys.COLLECTION_MAX_SIZE, innerListSize))
                .create();

        assertThat(result)
                .hasSize(rootListSize)
                .allSatisfy(innerList -> assertThat(innerList).hasSize(innerListSize));
    }

    @Nested
    @ExtendWith(InstancioExtension.class)
    class RootSelectorPrecedenceTest {

        @WithSettings
        private final Settings settings = Settings.create().set(Keys.MODE, Mode.LENIENT);

        @Test
        void rootGenerateVsSet() {
            final String result = Instancio.of(String.class)
                    .generate(root(), gen -> gen.text().pattern(FOO))
                    .set(allStrings(), BAR)
                    .create();

            assertThat(result).isEqualTo(FOO);
        }

        @Test
        void rootGenerateVsGenerate() {
            final String result = Instancio.of(String.class)
                    .generate(root(), gen -> gen.text().pattern(FOO))
                    .generate(allStrings(), gen -> gen.text().pattern(FOO))
                    .create();

            assertThat(result).isEqualTo(FOO);
        }

        @Test
        void rootSetVsSet() {
            final String result = Instancio.of(String.class)
                    .set(root(), FOO)
                    .set(allStrings(), BAR)
                    .create();

            assertThat(result).isEqualTo(FOO);
        }

        @Test
        void rootSetVsGenerate() {
            final String result = Instancio.of(String.class)
                    .set(root(), FOO)
                    .generate(allStrings(), gen -> gen.text().pattern(FOO))
                    .create();

            assertThat(result).isEqualTo(FOO);
        }

    }
}
