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
package org.instancio.test.features.assign.adhoc;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.constructor.StringsAbcCtor;
import org.instancio.test.support.pojo.interfaces.StringsAbcInterface;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.record.StringsAbcRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.Parameter;
import org.junit.jupiter.params.ParameterizedClass;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

@SuppressWarnings("NullAway")
@ParameterizedClass
@FieldSource("POJO_TYPES")
@FeatureTag({Feature.ASSIGN, Feature.INSTANTIATION_STRATEGIES})
@ExtendWith(InstancioExtension.class)
class AssignAbcTest {

    private static final String A_VAL = "A";
    private static final String B_VAL = "B";
    private static final String C_VAL = "C";

    private static final List<Class<?>> POJO_TYPES = List.of(
            StringsAbc.class,
            StringsAbcCtor.class,
            StringsAbcRecord.class
    );

    @Parameter(0)
    private Class<? extends StringsAbcInterface> rootClass;

    /**
     * Verifies assignments work with classes that have only default constructor,
     * or only all-args constructor where delayed values are passed-in as ctor args.
     *
     * @see #POJO_TYPES
     */
    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.INSTANTIATION_STRATEGIES, getInstantiationStrategies())
            .set(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL);

    private final TargetSelector A = getRandomSelectorForField("a");
    private final TargetSelector B = getRandomSelectorForField("b");
    private final TargetSelector C = getRandomSelectorForField("c");

    private static TargetSelector getRandomSelectorForField(final String fieldName) {
        final TargetSelector[] choices = {
                field(fieldName),
                fields().named(fieldName)
        };

        return Instancio.gen().oneOf(choices).get();
    }

    /**
     * Get instantiation strategies in random order, since Instancio uses the strategies
     * in the order they are specified.
     *
     * <p>Intentionally excludes `InstantiationStrategy.BYPASS_CONSTRUCTOR` (Unsafe/ReflectionFactory)
     */
    private InstantiationStrategies getInstantiationStrategies() {
        final Collection<InstantiationStrategy> instantiationStrategies = Instancio.gen()
                .shuffle(InstantiationStrategy.ALL_ARGS, InstantiationStrategy.NO_ARGS)
                .get();

        return InstantiationStrategies.of(instantiationStrategies.toArray(InstantiationStrategy[]::new));
    }

    @ValueSource(strings = {"a", "b", "c"})
    @ParameterizedTest
    void selfReferencing(final String fieldName) {
        final InstancioApi<?> api = Instancio.of(rootClass)
                .assign(Assign.given(field(rootClass, fieldName))
                        .satisfies(field -> true)
                        .set(field(rootClass, fieldName), "foo"));

        assertUnresolvedAssignmentException(api);
    }

    @Nested
    class FromATest {

        @Test
        @DisplayName("1: a → b   a → c")
        void givenASetB_GivenASetC() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("2: a → b   b → c")
        void givenASetB_GivenBSetC() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("3: a → b   b → a")
        void givenASetB_GivenBSetA() {
            final InstancioApi<?> api = Instancio.of(rootClass)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("4: a → b   c → a")
        void givenASetB_GivenCSetA() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("5: a → c   b → c")
        void givenASetC_GivenBSetC() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(A, A_VAL)
                    // NOTE maybe should throw error on duplicate assignment? need to explore use cases
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(B).satisfies(o -> true).set(C, "C#"))
                    .create();

            assertThat(result.getA()).isEqualTo(A_VAL);
            assertThat(result.getB()).hasSizeGreaterThan(1); // random value
            assertThat(result.getC()).isEqualTo("C#");
        }

        @Test
        @DisplayName("6: a → c   c → a")
        void givenASetC_GivenCSetA() {
            final InstancioApi<?> api = Instancio.of(rootClass)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("7: a → c   c → b")
        void givenASetC_GivenCSetB() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(A, A_VAL)
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("8: a → c   a → b")
        void givenASetC_GivenASetB() {
            StringsAbcInterface result = Instancio.of(rootClass)
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
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("10: b → a   a → c")
        void givenBSetA_GivenASetC() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("11: b → a   a → b")
        void givenBSetA_GivenASetB() {
            final InstancioApi<?> api = Instancio.of(rootClass)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("12: b → a   c → b")
        void givenBSetA_GivenCSetB() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("13: b → c   a → c")
        void givenBSetC_GivenASetC() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(A).satisfies(o -> true).set(C, "C#"))
                    .create();

            assertThat(result.getA()).hasSizeGreaterThan(1); // random value
            assertThat(result.getB()).isEqualTo(B_VAL);
            assertThat(result.getC()).as("Last conditional wins").isEqualTo("C#");
        }

        @Test
        @DisplayName("14: b → c   c → b")
        void givenBSetC_GivenCSetB() {
            final InstancioApi<?> api = Instancio.of(rootClass)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("15: b → c   c → a")
        void givenBSetC_GivenCSetA() {
            final StringsAbcInterface result = Instancio.of(rootClass)
                    .set(B, B_VAL)
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("16: b → c   b → a")
        void givenBSetC_GivenBSetA() {
            final StringsAbcInterface result = Instancio.of(rootClass)
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
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("18: c → a   a → b")
        void givenCSetA_GivenASetB() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(B, B_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("19: c → a   a → c")
        void givenCSetA_GivenASetC() {
            final InstancioApi<?> api = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(A).satisfies(A_VAL::equals).set(C, C_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("20: c → a   b → c")
        void givenCSetA_GivenBSetC() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(B, B_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("21: c → b   a → b")
        void givenCSetB_GivenASetB() {
            StringsAbcInterface result = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    // Since both assignments' predicates are satisfied,
                    // the last one should win
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(A).satisfies(o -> true).set(B, "B#"))
                    .create();

            assertThat(result.getA()).hasSizeGreaterThan(1); // random value
            assertThat(result.getB()).isEqualTo("B#");
            assertThat(result.getC()).isEqualTo("C");
        }

        @Test
        @DisplayName("22: c → b   b → c")
        void givenCSetB_GivenBSetC() {
            final InstancioApi<?> api = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(C, C_VAL));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        @DisplayName("23: c → b   b → a")
        void givenCSetB_GivenBSetA() {
            final StringsAbcInterface result = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(B).satisfies(B_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }

        @Test
        @DisplayName("24: c → b   c → a")
        void givenCSetB_GivenCSetA() {
            final StringsAbcInterface result = Instancio.of(rootClass)
                    .set(C, C_VAL)
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(B, B_VAL))
                    .assign(Assign.given(C).satisfies(C_VAL::equals).set(A, A_VAL))
                    .create();

            assertResult(result);
        }
    }

    private void assertResult(final StringsAbcInterface result) {
        assertThat(result.getA()).isEqualTo(A_VAL);
        assertThat(result.getB()).isEqualTo(B_VAL);
        assertThat(result.getC()).isEqualTo(C_VAL);
    }

    private static void assertUnresolvedAssignmentException(final InstancioApi<?> api) {
        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("unresolved assignment");
    }
}
