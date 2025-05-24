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

package org.instancio.test.features.generator.text;

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATE, Feature.WORD_GENERATOR})
@ExtendWith(InstancioExtension.class)
class WordGeneratorTest {

    private static final int NUM_NOUNS = 5174;
    private static final int NUM_VERBS = 1860;
    private static final int NUM_ADJECTIVES = 1879;
    private static final int NUM_ADVERBS = 2927;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.MAX_GENERATION_ATTEMPTS, 1_000_000);

    @Test
    void anyWordClass() {
        final Set<String> results = Instancio.ofSet(String.class)
                .size(100)
                .generate(allStrings(), gen -> gen.text().word())
                .create();

        // Since word type is not specified, results should contain
        // a mix of nouns, verbs, adjectives, and adverbs.
        // No simple way to verify this though, so just check for size...
        assertThat(results).hasSize(100);
    }

    @Test
    void noun() {
        assertWordCount(NUM_NOUNS, gen -> gen.text().word().noun());
    }

    @Test
    void verb() {
        assertWordCount(NUM_VERBS, gen -> gen.text().word().verb());
    }

    @Test
    void adjective() {
        assertWordCount(NUM_ADJECTIVES, gen -> gen.text().word().adjective());
    }

    @Test
    void adverb() {
        assertWordCount(NUM_ADVERBS, gen -> gen.text().word().adverb());
    }

    @Test
    void nullable() {
        final Set<StringHolder> results = Instancio.ofSet(StringHolder.class)
                .size(100)
                .generate(allStrings(), gen -> gen.text().word().noun().nullable())
                .create();

        assertThat(results)
                .extracting(StringHolder::getValue)
                .hasSize(100)
                .containsNull();
    }

    private static void assertWordCount(
            final int expectedCount,
            final GeneratorSpecProvider<String> spec) {

        final List<String> results = Instancio.ofList(String.class)
                .size(expectedCount)
                .withUnique(allStrings())
                .generate(allStrings(), spec)
                .create();

        assertThat(results).hasSize(expectedCount);
    }
}