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
package org.instancio.generator.lang;

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.random.DefaultRandom;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NumberGeneratorSpecTest {
    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), new DefaultRandom());

    @Nested
    class ByteGeneratorTest extends NumberGeneratorSpecTestTemplate<Byte> {
        @Override
        protected AbstractRandomNumberGeneratorSpec<Byte> createGenerator() {
            return new ByteGenerator(context);
        }

        @Override
        protected Map<Class<?>, Boolean> verifySupported() {
            return new HashMap<Class<?>, Boolean>() {{
                put(byte.class, true);
                put(Byte.class, true);
                put(byte[].class, false);
            }};
        }
    }

    @Nested
    class ShortGeneratorTest extends NumberGeneratorSpecTestTemplate<Short> {
        @Override
        protected AbstractRandomNumberGeneratorSpec<Short> createGenerator() {
            return new ShortGenerator(context);
        }

        @Override
        protected Map<Class<?>, Boolean> verifySupported() {
            return new HashMap<Class<?>, Boolean>() {{
                put(short.class, true);
                put(Short.class, true);
                put(short[].class, false);
            }};
        }
    }

    @Nested
    class IntegerGeneratorTest extends NumberGeneratorSpecTestTemplate<Integer> {
        @Override
        protected AbstractRandomNumberGeneratorSpec<Integer> createGenerator() {
            return new IntegerGenerator(context);
        }

        @Override
        protected Map<Class<?>, Boolean> verifySupported() {
            return new HashMap<Class<?>, Boolean>() {{
                put(int.class, true);
                put(Integer.class, true);
                put(int[].class, false);
            }};
        }
    }

    @Nested
    class LongGeneratorTest extends NumberGeneratorSpecTestTemplate<Long> {
        @Override
        protected AbstractRandomNumberGeneratorSpec<Long> createGenerator() {
            return new LongGenerator(context);
        }

        @Override
        protected Map<Class<?>, Boolean> verifySupported() {
            return new HashMap<Class<?>, Boolean>() {{
                put(long.class, true);
                put(Long.class, true);
                put(long[].class, false);
            }};
        }
    }

    @Nested
    class FloatGeneratorTest extends NumberGeneratorSpecTestTemplate<Float> {
        @Override
        protected AbstractRandomNumberGeneratorSpec<Float> createGenerator() {
            return new FloatGenerator(context);
        }

        @Override
        protected Map<Class<?>, Boolean> verifySupported() {
            return new HashMap<Class<?>, Boolean>() {{
                put(float.class, true);
                put(Float.class, true);
                put(float[].class, false);
            }};
        }

        @Test
        void rangeWithFractionalValues() {
            final AbstractRandomNumberGeneratorSpec<Float> generator = getGenerator();
            final Float min = 1.2f;
            final Float max = 1.4f;
            generator.range(min, max);
            assertThat(generator.generate(new DefaultRandom())).isBetween(min, max);
        }
    }

    @Nested
    class DoubleGeneratorTest extends NumberGeneratorSpecTestTemplate<Double> {
        @Override
        protected AbstractRandomNumberGeneratorSpec<Double> createGenerator() {
            return new DoubleGenerator(context);
        }

        @Override
        protected Map<Class<?>, Boolean> verifySupported() {
            return new HashMap<Class<?>, Boolean>() {{
                put(double.class, true);
                put(Double.class, true);
                put(double[].class, false);
            }};
        }

        @Test
        void rangeWithFractionalValues() {
            final AbstractRandomNumberGeneratorSpec<Double> generator = getGenerator();
            final Double min = 1.2;
            final Double max = 1.4;
            generator.range(min, max);
            assertThat(generator.generate(new DefaultRandom())).isBetween(min, max);
        }
    }
}
