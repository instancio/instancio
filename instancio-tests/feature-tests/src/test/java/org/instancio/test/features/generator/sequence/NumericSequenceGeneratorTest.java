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
package org.instancio.test.features.generator.sequence;

import org.instancio.Instancio;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class NumericSequenceGeneratorTest {

    @Nested
    @DisplayName("Each field has its own sequence")
    class EachFieldIncrementedSeparatelyTest {
        @Test
        void ofList() {
            final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                    .size(3)
                    .generate(field(IntegerHolder::getPrimitive), Generators::intSeq)
                    .generate(field(IntegerHolder::getWrapper), Generators::intSeq)
                    .create();

            assertEachField123(result);
        }

        @Test
        void ofListGroupSelector() {
            final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                    .size(3)
                    .generate(all(
                            field(IntegerHolder::getPrimitive),
                            field(IntegerHolder::getWrapper)), Generators::intSeq)
                    .create();

            assertEachField123(result);
        }

        @Test
        void ofListAllInts() {
            final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                    .size(3)
                    .generate(allInts(), Generators::intSeq)
                    .create();

            assertEachField123(result);
        }

        @Test
        void stream() {
            final List<IntegerHolder> result = Instancio.of(IntegerHolder.class)
                    .generate(field(IntegerHolder::getPrimitive), Generators::intSeq)
                    .generate(field(IntegerHolder::getWrapper), Generators::intSeq)
                    .stream()
                    .limit(3)
                    .collect(Collectors.toList());

            assertEachField123(result);
        }

        private void assertEachField123(final List<IntegerHolder> result) {
            assertThat(result).extracting(IntegerHolder::getPrimitive).containsExactly(1, 2, 3);
            assertThat(result).extracting(IntegerHolder::getWrapper).containsExactly(1, 2, 3);
        }
    }

    @Test
    void ofIntegerList() {
        final List<Integer> result = Instancio.ofList(Integer.class)
                .size(3)
                .generate(allInts(), Generators::intSeq)
                .create();

        assertThat(result).containsExactly(1, 2, 3);
    }

    @Test
    void ofListWithPredicateSelector() {
        final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                .size(3)
                .generate(fields(f -> f.getType() == int.class || f.getType() == Integer.class),
                        Generators::intSeq)
                .create();

        assertThat(result).extracting(IntegerHolder::getPrimitive).containsExactly(1, 3, 5);
        assertThat(result).extracting(IntegerHolder::getWrapper).containsExactly(2, 4, 6);
    }
}
