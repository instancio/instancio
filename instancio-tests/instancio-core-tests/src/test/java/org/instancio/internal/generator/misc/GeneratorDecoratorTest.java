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
package org.instancio.internal.generator.misc;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratorDecoratorTest {

    private static final GeneratorContext CONTEXT = new GeneratorContext(Settings.defaults(), new DefaultRandom());

    private static final PopulateAction ACTION_TO_SET = PopulateAction.APPLY_SELECTORS;

    private static class DummyGenerator implements Generator<Object> {
        private final Hints hints;

        private DummyGenerator(final Hints hints) {
            this.hints = hints;
        }

        @Override
        public Object generate(final Random random) {
            return null;
        }

        @Override
        public Hints hints() {
            return hints;
        }
    }

    @Test
    void decorateGeneratorWithAction() {
        final Generator<?> original = new DummyGenerator(Hints.withPopulateAction(PopulateAction.NONE));
        final Generator<?> decorated = GeneratorDecorator.decorateActionless(original, PopulateAction.ALL, CONTEXT);

        assertThat(decorated)
                .as("Generator should not be decorated if it has PopulateAction hint")
                .isSameAs(original);
    }

    @Test
    void otherHintsArePreserved() {
        final InternalGeneratorHint hint = InternalGeneratorHint.builder().build();

        final Generator<?> original = new DummyGenerator(Hints.builder()
                .with(hint)
                .build());

        final Generator<?> decorated = GeneratorDecorator.decorateActionless(original, ACTION_TO_SET, CONTEXT);

        assertThat(decorated).isNotSameAs(original)
                .isExactlyInstanceOf(GeneratorDecorator.class)
                .extracting(Generator::hints)
                .satisfies(hints -> assertThat(hints.get(InternalGeneratorHint.class))
                        .as("Other hints should be preserved")
                        .isSameAs(hint))
                .extracting(Hints::populateAction)
                .isEqualTo(ACTION_TO_SET);
    }

    @Test
    void decorateGeneratorWithoutPopulateAction() {
        assertDecorated(new DummyGenerator(null));
        assertDecorated(new DummyGenerator(Hints.builder().build()));
    }

    private static void assertDecorated(final Generator<?> original) {
        final Generator<?> decorated = GeneratorDecorator.decorateActionless(original, ACTION_TO_SET, CONTEXT);

        assertThat(decorated).isNotSameAs(original)
                .isExactlyInstanceOf(GeneratorDecorator.class)
                .extracting(Generator::hints)
                .extracting(Hints::populateAction)
                .isEqualTo(ACTION_TO_SET);
    }
}
