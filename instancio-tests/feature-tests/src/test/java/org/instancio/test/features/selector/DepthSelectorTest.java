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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

/**
 * Verifies using {@code atDepth()} with:
 *
 * <ul>
 *   <li>regular class selector</li>
 *   <li>primitive/wrapper selector</li>
 *   <li>predicate type selector</li>
 *   <li>predicate field selector builder</li>
 *   <li>predicate type selector builder</li>
 * </ul>
 */
@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.SELECTOR, Feature.DEPTH_SELECTOR})
@ExtendWith(InstancioExtension.class)
class DepthSelectorTest {

    private static final Integer EXPECTED = -12345;

    private static Stream<Arguments> depth1Selectors() {
        final int depth = 1;
        final Predicate<Integer> predicate = d -> d == depth;
        return Stream.of(
                Arguments.of(all(Integer.class).atDepth(depth)),
                Arguments.of(allInts().atDepth(depth)),
                Arguments.of(field(Pojo0::getVal).atDepth(depth)),
                Arguments.of(fields().ofType(Integer.class).atDepth(depth)),
                Arguments.of(types(t -> t == Integer.class).atDepth(depth)),

                // predicate depth
                Arguments.of(fields(f -> f.getType() == Integer.class).atDepth(predicate)),
                Arguments.of(fields().ofType(Integer.class).atDepth(predicate)),
                Arguments.of(types().of(Integer.class).atDepth(predicate))
        );
    }

    private static Stream<Arguments> depthGreaterThanOneSelectors() {
        final Predicate<Integer> predicate = d -> d > 1;
        return Stream.of(
                Arguments.of(types(t -> t == Integer.class).atDepth(predicate)),
                Arguments.of(fields(f -> f.getType() == Integer.class).atDepth(predicate)),
                Arguments.of(fields().ofType(Integer.class).atDepth(predicate)),
                Arguments.of(types().of(Integer.class).atDepth(predicate))
        );
    }

    @MethodSource("depth1Selectors")
    @ParameterizedTest
    void depth1(final TargetSelector selector) {
        final Pojo0 result = Instancio.of(Pojo0.class)
                .set(selector, EXPECTED)
                .create();

        assertThat(result.val).isEqualTo(EXPECTED);
        assertThat(result.pojo1.val).isNotEqualTo(EXPECTED);
        assertThat(result.pojo1.pojo2.val).isNotEqualTo(EXPECTED);
        assertThat(result.pojo1.pojo2.pojo3.val).isNotEqualTo(EXPECTED);
    }

    @MethodSource("depthGreaterThanOneSelectors")
    @ParameterizedTest
    void depthGreaterThanOne(final TargetSelector selector) {
        final Pojo0 result = Instancio.of(Pojo0.class)
                .set(selector, EXPECTED)
                .create();

        assertThat(result.val).isNotEqualTo(EXPECTED);
        assertThat(result.pojo1.val).isEqualTo(EXPECTED);
        assertThat(result.pojo1.pojo2.val).isEqualTo(EXPECTED);
        assertThat(result.pojo1.pojo2.pojo3.val).isEqualTo(EXPECTED);
    }

    @Test
    void depthZeroShouldTargetTheRoot() {
        final List<Pojo0> result = Instancio.ofList(Pojo0.class)
                .set(all(List.class).atDepth(0), null)
                .create();

        assertThat(result).isNull();
    }

    @Nested
    class DepthSelectorPrecedenceTest {

        @Test
        void differentValuePerDepthWithRegularSelectors() {
            final Pojo0 result = Instancio.of(Pojo0.class)
                    .set(allInts().atDepth(3), 3)
                    .set(allInts().atDepth(4), 4)
                    .set(allInts().atDepth(1), 1)
                    .set(allInts().atDepth(2), 2)
                    .create();

            assertThat(result.val).isEqualTo(1);
            assertThat(result.pojo1.val).isEqualTo(2);
            assertThat(result.pojo1.pojo2.val).isEqualTo(3);
            assertThat(result.pojo1.pojo2.pojo3.val).isEqualTo(4);
        }

        @Test
        void differentValuePerDepthWithPredicateSelectors() {
            final Pojo0 result = Instancio.of(Pojo0.class)
                    .set(types().of(Integer.class).atDepth(3), 3)
                    .set(types().of(Integer.class).atDepth(4), 4)
                    .set(types().of(Integer.class).atDepth(1), 1)
                    .set(types().of(Integer.class).atDepth(2), 2)
                    .create();

            assertThat(result.val).isEqualTo(1);
            assertThat(result.pojo1.val).isEqualTo(2);
            assertThat(result.pojo1.pojo2.val).isEqualTo(3);
            assertThat(result.pojo1.pojo2.pojo3.val).isEqualTo(4);
        }

        @Test
        void lastSelectorWins1() {
            final Pojo0 result = Instancio.of(Pojo0.class)
                    .set(allInts(), 1)
                    .set(allInts().atDepth(3), 3)
                    .create();

            assertThat(result.val).isEqualTo(1);
            assertThat(result.pojo1.val).isEqualTo(1);
            assertThat(result.pojo1.pojo2.val).isEqualTo(3);
            assertThat(result.pojo1.pojo2.pojo3.val).isEqualTo(1);
        }

        @Test
        void lastSelectorWins2() {
            final Pojo0 result = Instancio.of(Pojo0.class)
                    .set(allInts().atDepth(3), 3) // unused selector, since allInts() wins
                    .set(allInts(), 1)
                    .lenient()
                    .create();

            assertThat(result.val).isEqualTo(1);
            assertThat(result.pojo1.val).isEqualTo(1);
            assertThat(result.pojo1.pojo2.val).isEqualTo(1);
            assertThat(result.pojo1.pojo2.pojo3.val).isEqualTo(1);
        }
    }

    private static class Pojo0 {
        private Integer val;
        private Pojo1 pojo1;

        Integer getVal() {
            return val;
        }
    }

    private static class Pojo1 {
        private Integer val;
        private Pojo2 pojo2;
    }

    private static class Pojo2 {
        private Integer val;
        private Pojo3 pojo3;
    }

    private static class Pojo3 {
        private Integer val;
    }
}
