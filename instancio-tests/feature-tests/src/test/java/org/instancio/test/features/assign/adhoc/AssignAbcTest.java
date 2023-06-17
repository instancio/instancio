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
package org.instancio.test.features.assign.adhoc;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Selector;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignAbcTest {

    private static final String A_VAL = "A";
    private static final String B_VAL = "B";
    private static final String C_VAL = "C";

    private static final Selector A = field(StringsAbc::getA);
    private static final Selector B = field(StringsAbc::getB);
    private static final Selector C = field(StringsAbc::getC);

    @ValueSource(strings = {"a", "b", "c"})
    @ParameterizedTest
    void selfReferencing(final String fieldName) {
        final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                .assign(Assign.given(field(StringsAbc.class, fieldName))
                        .satisfies(a -> true)
                        .set(field(StringsAbc.class, fieldName), "foo"));

        assertUnresolvedAssignmentException(api);
    }

    @Nested
    class FromATest {

        @Test
        @DisplayName("1: a → b   a → c")
        void givenASetB_GivenASetC() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("2: a → b   b → c")
        void givenASetB_GivenBSetC() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("3: a → b   b → a")
        void givenASetB_GivenBSetA() {
            final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("4: a → b   c → a")
        void givenASetB_GivenCSetA() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("5: a → c   b → c")
        void givenASetC_GivenBSetC() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(A, A_VAL)
                    // NOTE maybe should throw error on duplicate assignment? need to explore use cases
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(B).satisfies(o -> true).set(C, "C#"))
                    .create();

            assertThat(result.a).isEqualTo(A_VAL);
            assertThat(result.b).hasSizeGreaterThan(1); // random value
            assertThat(result.c).isEqualTo("C#");
        }

        @Test
        @DisplayName("6: a → c   c → a")
        void givenASetC_GivenCSetA() {
            final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("7: a → c   c → b")
        void givenASetC_GivenCSetB() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("8: a → c   a → b")
        void givenASetC_GivenASetB() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }
    }

    @Nested
    class FromBTest {

        @Test
        @DisplayName("9: b → a   b → c")
        void givenBSetA_GivenBSetC() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("10: b → a   a → c")
        void givenBSetA_GivenASetC() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("11: b → a   a → b")
        void givenBSetA_GivenASetB() {
            final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("12: b → a   c → b")
        void givenBSetA_GivenCSetB() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("13: b → c   a → c")
        void givenBSetC_GivenASetC() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(A).satisfies(o -> true).set(C, "C#"))
                    .create();

            assertThat(result.a).hasSizeGreaterThan(1); // random value
            assertThat(result.b).isEqualTo(B_VAL);
            assertThat(result.c).as("Last conditional wins").isEqualTo("C#");
        }

        @Test
        @DisplayName("14: b → c   c → b")
        void givenBSetC_GivenCSetB() {
            final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("15: b → c   c → a")
        void givenBSetC_GivenCSetA() {
            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("16: b → c   b → a")
        void givenBSetC_GivenBSetA() {
            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }
    }

    @Nested
    class FromCTest {

        @Test
        @DisplayName("17: c → a   c → b")
        void givenCSetA_GivenCSetB() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("18: c → a   a → b")
        void givenCSetA_GivenASetB() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("19: c → a   a → c")
        void givenCSetA_GivenASetC() {
            final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("20: c → a   b → c")
        void givenCSetA_GivenBSetC() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(B, B_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("21: c → b   a → b")
        void givenCSetB_GivenASetB() {
            StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    // Since both assignments' predicates are satisfied,
                    // the last one should win
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(A).satisfies(o -> true).set(B, "B#"))
                    .create();

            assertThat(result.a).hasSizeGreaterThan(1); // random value
            assertThat(result.b).isEqualTo("B#");
            assertThat(result.c).isEqualTo("C");
        }

        @Test
        @DisplayName("22: c → b   b → c")
        void givenCSetB_GivenBSetC() {
            final InstancioApi<StringsAbc> api = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("23: c → b   b → a")
        void givenCSetB_GivenBSetA() {
            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("24: c → b   c → a")
        void givenCSetB_GivenCSetA() {
            final StringsAbc result = Instancio.of(StringsAbc.class)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }
    }

    private static void assertResult(final StringsAbc result) {
        assertThat(result.a).isEqualTo(A_VAL);
        assertThat(result.b).isEqualTo(B_VAL);
        assertThat(result.c).isEqualTo(C_VAL);
    }

    private static void assertUnresolvedAssignmentException(final InstancioApi<?> api) {
        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("unresolved assignment");
    }
}
