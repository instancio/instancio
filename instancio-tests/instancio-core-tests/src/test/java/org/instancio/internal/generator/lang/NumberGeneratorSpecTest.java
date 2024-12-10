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
package org.instancio.internal.generator.lang;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.AbstractGeneratorTestTemplate;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NumberGeneratorSpecTest {
    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), new DefaultRandom());

    @Nested
    class ByteGeneratorTest extends AbstractGeneratorTestTemplate<Byte, ByteGenerator> {

        @Override
        protected String getApiMethod() {
            return "bytes()";
        }

        @Override
        protected ByteGenerator generator() {
            return new ByteGenerator(context);
        }
    }

    @Nested
    class ShortGeneratorTest extends AbstractGeneratorTestTemplate<Short, ShortGenerator> {

        @Override
        protected String getApiMethod() {
            return "shorts()";
        }

        @Override
        protected ShortGenerator generator() {
            return new ShortGenerator(context);
        }
    }

    @Nested
    class IntegerGeneratorTest extends AbstractGeneratorTestTemplate<Integer, IntegerGenerator> {

        @Override
        protected String getApiMethod() {
            return "ints()";
        }

        @Override
        protected IntegerGenerator generator() {
            return new IntegerGenerator(context);
        }
    }

    @Nested
    class LongGeneratorTest extends AbstractGeneratorTestTemplate<Long, LongGenerator> {

        @Override
        protected String getApiMethod() {
            return "longs()";
        }

        @Override
        protected LongGenerator generator() {
            return new LongGenerator(context);
        }
    }

    @Nested
    class FloatGeneratorTest extends AbstractGeneratorTestTemplate<Float, FloatGenerator> {

        @Override
        protected String getApiMethod() {
            return "floats()";
        }

        @Override
        protected FloatGenerator generator() {
            return new FloatGenerator(context);
        }

        @Test
        void rangeWithFractionalValues() {
            final FloatGenerator generator = generator();
            final Float min = 1.2f;
            final Float max = 1.4f;
            generator.range(min, max);
            assertThat(generator.generate(random)).isBetween(min, max);
        }
    }

    @Nested
    class DoubleGeneratorTest extends AbstractGeneratorTestTemplate<Double, DoubleGenerator> {

        @Override
        protected String getApiMethod() {
            return "doubles()";
        }

        @Override
        protected DoubleGenerator generator() {
            return new DoubleGenerator(context);
        }

        @Test
        void rangeWithFractionalValues() {
            final DoubleGenerator generator = generator();
            final Double min = 1.2;
            final Double max = 1.4;
            generator.range(min, max);
            assertThat(generator.generate(random)).isBetween(min, max);
        }
    }
}
