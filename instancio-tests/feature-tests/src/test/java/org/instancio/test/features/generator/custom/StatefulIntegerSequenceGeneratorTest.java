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
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfInteger;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.scope;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class StatefulIntegerSequenceGeneratorTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, 2)
            .set(Keys.COLLECTION_MAX_SIZE, 2)
            .lock();

    private static final int START_FROM = 10;

    private final Generator<Integer> incrementingGenerator = new Generator<Integer>() {

        private int current;

        @Override
        public void init(final GeneratorContext context) {
            current = START_FROM; // reset on each call to initialise()
        }

        @Override
        public Integer generate(final Random random) {
            return current++;
        }
    };

    @Test
    void statefulGenerator() {
        assertSequence(incrementingGenerator, START_FROM);

        // when the generator is passed to another invocation of Instancio.of(),
        // init should be called again, resetting the sequence
        assertSequence(incrementingGenerator, START_FROM);
    }

    private static void assertSequence(final Generator<?> generator, final int startingValue) {
        final TwoListsOfInteger result = Instancio.of(TwoListsOfInteger.class)
                .supply(all(Integer.class).within(scope(TwoListsOfInteger.class, "list1")), generator)
                .supply(all(Integer.class).within(scope(TwoListsOfInteger.class, "list2")), generator)
                .create();

        assertThat(result.getList1()).containsExactly(startingValue, startingValue + 1);
        assertThat(result.getList2())
                .as("Generator sequence should continue if used with multiple fields")
                .containsExactly(startingValue + 2, startingValue + 3);
    }
}
