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
package org.instancio.test.features.instantiation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.CTOR_PREFIX;

// NOTE: classes are intentionally not records to verify handling of POJOs.
// Some fields are also intentionally not final.
@SuppressWarnings({"ClassCanBeRecord", "FieldMayBeFinal", "unused"})
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class ConstructorPopulationTest {

    @Nested
    class MatchingParameterNamesTest {

        /**
         * Every field is a constructor parameter; one final, one not.
         */
        private static final class AllArgs {
            private final String finalValue;
            private String nonFinalValue;

            private AllArgs(final String finalValue, final String nonFinalValue) {
                this.finalValue = CTOR_PREFIX + finalValue;
                this.nonFinalValue = CTOR_PREFIX + nonFinalValue;
            }
        }

        /**
         * Some fields are constructor parameters, one is not.
         */
        private static final class PartialArgs {
            private final String finalValue;
            private String nonFinalValue;
            private @Nullable String third; // not a constructor parameter

            private PartialArgs(final String finalValue, final String nonFinalValue) {
                this.finalValue = CTOR_PREFIX + finalValue;
                this.nonFinalValue = CTOR_PREFIX + nonFinalValue;
            }
        }

        /**
         * The non-parameter field is final and assigned by the constructor body.
         */
        private static final class FinalFieldSetByConstructorBody {
            private final String name;   // constructor parameter
            private final String status; // NOT a parameter; initialised by the body

            private FinalFieldSetByConstructorBody(final String name) {
                this.name = CTOR_PREFIX + name;
                this.status = "NEW";
            }
        }

        @Test
        void allArgsConstructor() {
            final AllArgs result = Instancio.create(AllArgs.class);

            assertThat(result.finalValue).startsWith(CTOR_PREFIX);
            assertThat(result.nonFinalValue).startsWith(CTOR_PREFIX);
        }

        @Test
        void partialArgsConstructor() {
            final PartialArgs result = Instancio.create(PartialArgs.class);

            assertThat(result.finalValue).startsWith(CTOR_PREFIX);
            assertThat(result.nonFinalValue).startsWith(CTOR_PREFIX);
            assertThat(result.third).is(Conditions.RANDOM_STRING);
        }

        @Test
        void finalNonParameterFieldIsOverwrittenAfterConstruction() {
            final FinalFieldSetByConstructorBody result = Instancio.create(FinalFieldSetByConstructorBody.class);

            assertThat(result.name).startsWith(CTOR_PREFIX);
            assertThat(result.status).is(Conditions.RANDOM_STRING);
        }
    }

    @Nested
    class NonMatchingParameterNamesTest {

        private static final class MixedFields {
            private final String finalValue;
            private String nonFinalValue;
            private @Nullable String third; // not a constructor parameter

            private MixedFields(final String x, final String y) {
                this.finalValue = CTOR_PREFIX + x;
                this.nonFinalValue = CTOR_PREFIX + y;
            }
        }

        @Test
        void constructorIsNotUsedAndFieldsAreAssignedDirectly() {
            final MixedFields result = Instancio.create(MixedFields.class);

            assertThat(result.finalValue).is(Conditions.RANDOM_STRING);
            assertThat(result.nonFinalValue).is(Conditions.RANDOM_STRING);
            assertThat(result.third).is(Conditions.RANDOM_STRING);
        }
    }

    @Nested
    class ParameterTypeMatchingTest {

        private static final class SupertypeCollectionParameter {
            private final String stringValue;
            private final Set<String> tags;

            private SupertypeCollectionParameter(final String stringValue, final Collection<String> tags) {
                this.stringValue = CTOR_PREFIX + stringValue;
                this.tags = Set.copyOf(tags);
            }
        }

        private static final class SupertypeNumberParameter {
            private final String stringValue;
            private final int count;

            private SupertypeNumberParameter(final String stringValue, final Number count) {
                this.stringValue = CTOR_PREFIX + stringValue;
                this.count = count.intValue();
            }
        }

        private static final class SubtypeParameter {
            private final String stringValue;
            private final Collection<String> tags;

            private SubtypeParameter(final String stringValue, final Set<String> tags) {
                this.stringValue = CTOR_PREFIX + stringValue;
                this.tags = tags;
            }
        }

        private static final class WideningPrimitiveParameter {
            private final String stringValue;
            private final int count;

            private WideningPrimitiveParameter(final String stringValue, final long count) {
                this.stringValue = CTOR_PREFIX + stringValue;
                this.count = (int) count;
            }
        }

        @Test
        void parameterThatIsSupertypeOfFieldType() {
            final SupertypeCollectionParameter result = Instancio.create(SupertypeCollectionParameter.class);

            assertThat(result.stringValue).startsWith(CTOR_PREFIX);
            assertThat(result.tags).isNotEmpty();
        }

        @Test
        void parameterThatIsSupertypeOfBoxedFieldType() {
            final SupertypeNumberParameter result = Instancio.create(SupertypeNumberParameter.class);

            assertThat(result.stringValue).startsWith(CTOR_PREFIX);
            assertThat(result.count).is(Conditions.RANDOM_INTEGER);
        }

        @Test
        void parameterThatIsSubtypeOfFieldTypeFallsBackToFieldAssignment() {
            final SubtypeParameter result = Instancio.create(SubtypeParameter.class);

            assertThat(result.stringValue).is(Conditions.RANDOM_STRING);
            assertThat(result.tags).isNotEmpty();
        }

        @Test
        void widerPrimitiveParameterFallsBackToFieldAssignment() {
            final WideningPrimitiveParameter result = Instancio.create(WideningPrimitiveParameter.class);

            assertThat(result.stringValue).is(Conditions.RANDOM_STRING);
            assertThat(result.count).is(Conditions.RANDOM_INTEGER);
        }
    }

    @Nested
    class ParameterNotStoredByConstructorTest {

        private static final class DropsParameter {
            private final String kept;
            private @Nullable String dropped;

            private DropsParameter(final String kept, @Nullable final String dropped) {
                this.kept = CTOR_PREFIX + kept;
                // NOTE: `dropped` field is intentionally ignored
            }
        }

        @Test
        void parameterGeneratedButDroppedByConstructor() {
            final DropsParameter result = Instancio.create(DropsParameter.class);

            assertThat(result.kept).startsWith(CTOR_PREFIX);
            assertThat(result.dropped).isNull();
        }
    }
}
