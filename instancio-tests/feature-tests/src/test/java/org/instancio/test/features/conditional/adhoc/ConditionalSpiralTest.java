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
package org.instancio.test.features.conditional.adhoc;

import org.instancio.Conditional;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Selector;
import org.instancio.internal.util.ArrayUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.support.DefaultRandom;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.valueOf;

/**
 * Tests for conditionals with origins and destinations at different depths.
 *
 * <p>Note: this test is for documentation purposes only since conditionals
 * are only supported top-down (origin must be generated before destination).
 *
 * <p>While this test currently passes, it may not in the future.
 * In addition, it would fail if POJOs were replaced with records.
 */
@FeatureTag(Feature.CONDITIONAL)
@ExtendWith(InstancioExtension.class)
class ConditionalSpiralTest {

    private static final String
            A_VAL = "A",
            B_VAL = "B",
            C_VAL = "C",
            D_VAL = "D",
            E_VAL = "E",
            F_VAL = "F",
            G_VAL = "G",
            H_VAL = "H",
            I_VAL = "I";

    private static final Selector
            A = field(StringsAbc::getA),
            B = field(StringsAbc::getB),
            C = field(StringsAbc::getC),
            D = field(StringsDef::getD),
            E = field(StringsDef::getE),
            F = field(StringsDef::getF),
            G = field(StringsGhi::getG),
            H = field(StringsGhi::getH),
            I = field(StringsGhi::getI);

    /**
     * Starting from a known value at {@code a},
     * spiral inward assigning expected values.
     *
     * <pre>
     *            a >--------------+
     *  +-------> b >------------+ |
     *  | +-----> c >--------+ | | |
     *  | | +------> d >---+ | | | |
     *  | | |  +---> e     | | | | |
     *  | | |  +---< f <---+ | | | |
     *  | | +----------< g <-+ | | |
     *  | +------------< h <-----+ |
     *  +--------------< i <-------+
     * </pre>
     */
    @MethodSource("spiralInArgs")
    @ParameterizedTest
    void spiralIn(final List<Conditional> conditionals) {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .set(A, A_VAL);

        conditionals.forEach(api::when);

        assertValues(api.create());
    }

    @MethodSource("spiralInArgs")
    @ParameterizedTest
    void spiralInWithOfList(final List<Conditional> conditionals) {
        final InstancioApi<List<StringsAbc>> api = Instancio.ofList(StringsAbc.class)
                .size(Constants.SAMPLE_SIZE_DDD)
                .set(A, A_VAL);

        conditionals.forEach(api::when);

        final List<StringsAbc> results = api.create();

        results.forEach(this::assertValues);
    }

    /**
     * Starting from a known value at {@code e},
     * spiral outward assigning expected values.
     */
    @MethodSource("spiralOutArgs")
    @ParameterizedTest
    void spiralOut(final List<Conditional> conditionals) {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .set(E, E_VAL);

        conditionals.forEach(api::when);

        assertValues(api.create());
    }

    @MethodSource("spiralOutArgs")
    @ParameterizedTest
    void spiralOutWithOfList(final List<Conditional> conditionals) {
        final InstancioApi<List<StringsAbc>> api = Instancio.ofList(StringsAbc.class)
                .size(Constants.SAMPLE_SIZE_DDD)
                .set(E, E_VAL);

        conditionals.forEach(api::when);

        final List<StringsAbc> results = api.create();

        results.forEach(this::assertValues);
    }

    private static Stream<Arguments> spiralInArgs() {
        return shuffleArgs(
                valueOf(A).is(A_VAL).set(I, I_VAL),
                valueOf(I).is(I_VAL).set(B, B_VAL),
                valueOf(B).is(B_VAL).set(H, H_VAL),
                valueOf(H).is(H_VAL).set(C, C_VAL),
                valueOf(C).is(C_VAL).set(G, G_VAL),
                valueOf(G).is(G_VAL).set(D, D_VAL),
                valueOf(D).is(D_VAL).set(F, F_VAL),
                valueOf(F).is(F_VAL).set(E, E_VAL));
    }

    private static Stream<Arguments> spiralOutArgs() {
        return shuffleArgs(
                valueOf(E).is(E_VAL).set(F, F_VAL),
                valueOf(F).is(F_VAL).set(D, D_VAL),
                valueOf(D).is(D_VAL).set(G, G_VAL),
                valueOf(G).is(G_VAL).set(C, C_VAL),
                valueOf(C).is(C_VAL).set(H, H_VAL),
                valueOf(H).is(H_VAL).set(B, B_VAL),
                valueOf(B).is(B_VAL).set(I, I_VAL),
                valueOf(I).is(I_VAL).set(A, A_VAL));
    }

    private static Stream<Arguments> shuffleArgs(final Conditional... conditionals) {
        ArrayUtils.shuffle(conditionals, new DefaultRandom());
        return Stream.of(Arguments.of(Arrays.asList(conditionals)));
    }

    private void assertValues(final StringsAbc result) {
        assertThat(result.a).isEqualTo(A_VAL);
        assertThat(result.b).isEqualTo(B_VAL);
        assertThat(result.c).isEqualTo(C_VAL);
        assertThat(result.def.d).isEqualTo(D_VAL);
        assertThat(result.def.e).isEqualTo(E_VAL);
        assertThat(result.def.f).isEqualTo(F_VAL);
        assertThat(result.def.ghi.g).isEqualTo(G_VAL);
        assertThat(result.def.ghi.h).isEqualTo(H_VAL);
        assertThat(result.def.ghi.i).isEqualTo(I_VAL);
    }
}
