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
package org.instancio.test.features.values;

import org.apache.commons.lang3.tuple.Pair;
import org.instancio.generator.specs.NumberSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Gen.bytes;
import static org.instancio.Gen.doubles;
import static org.instancio.Gen.floats;
import static org.instancio.Gen.ints;
import static org.instancio.Gen.longs;
import static org.instancio.Gen.shorts;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class NumberSpecTest {

    @Nested
    class ByteSpecTest extends AbstractNumberSpecTestTemplate<Byte> {
        @Override
        protected NumberSpec<Byte> spec() {
            return bytes();
        }

        @Override
        protected Pair<Byte, Byte> getRange() {
            return Pair.of((byte) 10, (byte) 15);
        }
    }

    @Nested
    class ShortSpecTest extends AbstractNumberSpecTestTemplate<Short> {
        @Override
        protected NumberSpec<Short> spec() {
            return shorts();
        }

        @Override
        protected Pair<Short, Short> getRange() {
            return Pair.of((short) 10, (short) 15);
        }
    }

    @Nested
    class IntegerSpecTest extends AbstractNumberSpecTestTemplate<Integer> {
        @Override
        protected NumberSpec<Integer> spec() {
            return ints();
        }

        @Override
        protected Pair<Integer, Integer> getRange() {
            return Pair.of(10, 15);
        }
    }

    @Nested
    class LongSpecTest extends AbstractNumberSpecTestTemplate<Long> {
        @Override
        protected NumberSpec<Long> spec() {
            return longs();
        }

        @Override
        protected Pair<Long, Long> getRange() {
            return Pair.of(10L, 15L);
        }
    }

    @Nested
    class FloatSpecTest extends AbstractNumberSpecTestTemplate<Float> {
        @Override
        protected NumberSpec<Float> spec() {
            return floats();
        }

        @Override
        protected Pair<Float, Float> getRange() {
            return Pair.of(10.1f, 14.9f);
        }
    }

    @Nested
    class DoubleSpecTest extends AbstractNumberSpecTestTemplate<Double> {
        @Override
        protected NumberSpec<Double> spec() {
            return doubles();
        }

        @Override
        protected Pair<Double, Double> getRange() {
            return Pair.of(10.1, 14.9);
        }
    }
}
