/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.generator.lang;

import org.instancio.generator.specs.NumberGeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.BaseNumericGeneratorTest;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class NumericGeneratorTest {

    @Nested
    class ByteGeneratorTest extends BaseNumericGeneratorTest<Byte> {

        ByteGeneratorTest() {
            super(Byte.class);
        }

        @Override
        public NumberGeneratorSpec<Byte> createSpec(final Generators gen) {
            return gen.bytes();
        }
    }

    @Nested
    class ShortGeneratorTest extends BaseNumericGeneratorTest<Short> {

        ShortGeneratorTest() {
            super(Short.class);
        }

        @Override
        public NumberGeneratorSpec<Short> createSpec(final Generators gen) {
            return gen.shorts();
        }
    }

    @Nested
    class IntegerGeneratorTest extends BaseNumericGeneratorTest<Integer> {

        IntegerGeneratorTest() {
            super(Integer.class);
        }

        @Override
        public NumberGeneratorSpec<Integer> createSpec(final Generators gen) {
            return gen.ints();
        }
    }

    @Nested
    class LongGeneratorTest extends BaseNumericGeneratorTest<Long> {

        LongGeneratorTest() {
            super(Long.class);
        }

        @Override
        public NumberGeneratorSpec<Long> createSpec(final Generators gen) {
            return gen.longs();
        }
    }

    @Nested
    class FloatGeneratorTest extends BaseNumericGeneratorTest<Float> {

        FloatGeneratorTest() {
            super(Float.class);
        }

        @Override
        public NumberGeneratorSpec<Float> createSpec(final Generators gen) {
            return gen.floats();
        }
    }

    @Nested
    class DoubleGeneratorTest extends BaseNumericGeneratorTest<Double> {

        DoubleGeneratorTest() {
            super(Double.class);
        }

        @Override
        public NumberGeneratorSpec<Double> createSpec(final Generators gen) {
            return gen.doubles();
        }
    }

}
