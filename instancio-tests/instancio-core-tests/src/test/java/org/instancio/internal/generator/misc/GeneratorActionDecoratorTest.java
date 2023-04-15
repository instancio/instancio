/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class GeneratorActionDecoratorTest {
    private static final AfterGenerate AFTER_GENERATE = AfterGenerate.APPLY_SELECTORS;

    /**
     * All internal generators extend from AbstractGenerator.
     */
    private static class DummyInternalGenerator extends AbstractGenerator<Object> {
        private DummyInternalGenerator() {
            super(null);
        }

        @Override
        public String apiMethod() {
            return null;
        }

        @Override
        protected Object tryGenerateNonNull(final Random random) {
            return null;
        }
    }

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
    void decorateGeneratorWithAfterGenerate() {
        final Generator<?> original = new DummyInternalGenerator();
        final Generator<?> decorated = GeneratorDecorator.decorateIfNullAfterGenerate(original, AfterGenerate.POPULATE_ALL);

        assertThat(decorated)
                .as("Internal generators should not be decorated")
                .isSameAs(original);
    }

    @Test
    void otherHintsArePreserved() {
        final InternalGeneratorHint hint = InternalGeneratorHint.builder().build();

        final Generator<?> original = new DummyGenerator(Hints.builder()
                .with(hint)
                .build());

        final Generator<?> decorated = GeneratorDecorator.decorateIfNullAfterGenerate(original, AFTER_GENERATE);

        assertThat(decorated).isNotSameAs(original)
                .isExactlyInstanceOf(GeneratorActionDecorator.class)
                .extracting(Generator::hints)
                .satisfies(hints -> assertThat(hints.get(InternalGeneratorHint.class))
                        .as("Other hints should be preserved")
                        .isSameAs(hint))
                .extracting(Hints::afterGenerate)
                .isEqualTo(AFTER_GENERATE);
    }

    @Test
    void decorateGeneratorWithoutAfterGenerate() {
        assertDecorated(new DummyGenerator(null));
        assertDecorated(new DummyGenerator(Hints.builder().build()));
    }

    private static void assertDecorated(final Generator<?> original) {
        final Generator<?> decorated = GeneratorDecorator.decorateIfNullAfterGenerate(original, AFTER_GENERATE);

        assertThat(decorated).isNotSameAs(original)
                .isExactlyInstanceOf(GeneratorActionDecorator.class)
                .extracting(Generator::hints)
                .extracting(Hints::afterGenerate)
                .isEqualTo(AFTER_GENERATE);
    }
}
