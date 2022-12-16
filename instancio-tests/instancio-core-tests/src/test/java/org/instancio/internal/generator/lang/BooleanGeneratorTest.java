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
package org.instancio.internal.generator.lang;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.instancio.testsupport.asserts.HintsAssert;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allBooleans;

@NonDeterministicTag
@FeatureTag(Feature.SETTINGS)
class BooleanGeneratorTest {
    private static final int SAMPLE_SIZE = 50;
    private static final Settings settings = Settings.defaults().set(Keys.BOOLEAN_NULLABLE, true);
    private static final Random random = new DefaultRandom();
    private static final GeneratorContext context = new GeneratorContext(settings, random);
    private final Generator<Boolean> generator = new BooleanGenerator(context);

    @Test
    void generate() {
        final Set<Object> results = new HashSet<>();
        for (int i = 0; i < SAMPLE_SIZE; i++) {
            results.add(generator.generate(random));
        }

        assertThat(results).containsNull()
                .as("true, false, and null")
                .hasSize(3);

        HintsAssert.assertHints(generator.hints()).afterGenerate(AfterGenerate.DO_NOT_MODIFY);
    }

    @Test
    void nullableViaGeneratorSpec() {
        final Stream<Boolean> results = Instancio.of(Boolean.class)
                .generate(allBooleans(), gen -> gen.booleans().nullable())
                .stream()
                .limit(SAMPLE_SIZE);

        assertThat(results).containsNull();
    }
}
