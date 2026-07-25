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

import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.When;
import org.instancio.generator.Generator;
import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.test.support.pojo.constructor.CyclicCtorPojo;
import org.instancio.test.support.pojo.constructor.MixedCtorPojo;
import org.instancio.test.support.pojo.constructor.StringsAbcCtor;
import org.instancio.test.support.pojo.constructor.StringsDefCtor;
import org.instancio.test.support.pojo.constructor.StringsGhiCtor;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * Verifies {@code assign()} against POJOs instantiated via constructor.
 *
 * <p>Values assigned to constructor parameters must be routed through
 * the constructor. A parameter whose value depends on another parameter
 * exercises the delayed-argument retry (similar to record components).
 */
@FeatureTag({Feature.ASSIGN, Feature.INSTANTIATION_STRATEGIES})
@ExtendWith(InstancioExtension.class)
class ConstructorAssignTest {

    private static final String EXPECTED = "foo";

    @Nested
    class BetweenConstructorParametersTest {

        @Test
        void originDeclaredBeforeDestination() {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsAbcCtor::getA).to(StringsAbcCtor::getB))
                    .create();

            assertThat(result.getB()).isEqualTo(result.getA());
        }

        @Test
        void originDeclaredAfterDestination() {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsAbcCtor::getC).to(StringsAbcCtor::getA))
                    .create();

            assertThat(result.getA()).isEqualTo(result.getC());
        }

        @Test
        void withValueTransformation() {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsAbcCtor::getA)
                            .to(StringsAbcCtor::getB)
                            .as((String s) -> "prefix-" + s))
                    .create();

            assertThat(result.getB()).isEqualTo("prefix-" + result.getA());
        }

        @MethodSource("assignmentVariants")
        @ParameterizedTest
        void valueOfWithSetGenerateSupply(final Assignment assignment) {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(assignment)
                    .create();

            assertThat(result.getDef().getGhi().getH()).isEqualTo(EXPECTED);
        }

        private static Stream<Arguments> assignmentVariants() {
            final Supplier<String> supplier = () -> EXPECTED;
            final Generator<String> generator = random -> EXPECTED;
            return Stream.of(
                    Arguments.of(valueOf(StringsGhiCtor::getH).set(EXPECTED)),
                    Arguments.of(valueOf(StringsGhiCtor::getH).generate(gen -> gen.oneOf(EXPECTED))),
                    Arguments.of(valueOf(StringsGhiCtor::getH).generate(Instancio.gen().oneOf(EXPECTED))),
                    Arguments.of(valueOf(StringsGhiCtor::getH).supply(supplier)),
                    Arguments.of(valueOf(StringsGhiCtor::getH).supply(generator)));
        }
    }

    @Nested
    class AcrossNestedObjectsTest {

        @Test
        void deeperOriginToShallowerDestination() {
            // `e` depends on `ghi.h`, which is generated later within the same constructor
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsGhiCtor::getH).to(StringsDefCtor::getE))
                    .create();

            assertThat(result.getDef().getE()).isEqualTo(result.getDef().getGhi().getH());
        }

        @Test
        void deeperOriginToTopLevelDestination() {
            // the top-level `a` depends on a value from
            // the `def.ghi` subtree, two constructors deep
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsGhiCtor::getH).to(StringsAbcCtor::getA))
                    .create();

            assertThat(result.getA()).isEqualTo(result.getDef().getGhi().getH());
        }

        @Test
        void shallowerOriginToDeeperDestination() {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsAbcCtor::getA).to(StringsGhiCtor::getI))
                    .create();

            assertThat(result.getDef().getGhi().getI()).isEqualTo(result.getA());
        }
    }

    @Nested
    class GivenConditionalTest {

        @Test
        void givenOriginDestinationWithElse() {
            final List<StringsAbcCtor> results = Instancio.ofList(StringsAbcCtor.class)
                    .size(Constants.SAMPLE_SIZE_DD)
                    .generate(field(StringsAbcCtor::getA), gen -> gen.oneOf("A1", "A2"))
                    .assign(given(field(StringsAbcCtor::getA), field(StringsAbcCtor::getB))
                            .set(When.is("A1"), "B1")
                            .elseSet("B2"))
                    .create();

            assertThat(results).hasSize(Constants.SAMPLE_SIZE_DD).allSatisfy(result -> {
                final String expectedB = "A1".equals(result.getA()) ? "B1" : "B2";
                assertThat(result.getB()).isEqualTo(expectedB);
            });
        }

        @Test
        void conditionalOnNestedParameter(@Given final String aVal) {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .set(field(StringsGhiCtor::getH), EXPECTED)
                    .assign(given(StringsGhiCtor::getH).satisfies(EXPECTED::equals)
                            .set(field(StringsAbcCtor::getA), aVal))
                    .create();

            assertThat(result.getA()).isEqualTo(aVal);
        }
    }

    @Nested
    class MixedFieldsTest {

        @Test
        void constructorParameterToSetterField() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .assign(valueOf(MixedCtorPojo::getFinalCtorField)
                            .to(MixedCtorPojo::getWithSetter))
                    .create();

            assertThat(result.getWithSetter()).isEqualTo(result.getFinalCtorField());
        }

        @Test
        void constructorParameterToFieldWithoutSetter() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .assign(valueOf(MixedCtorPojo::getFinalCtorField)
                            .to(MixedCtorPojo::getWithoutSetter))
                    .create();

            assertThat(result.getWithoutSetter()).isEqualTo(result.getFinalCtorField());
        }

        @Test
        void betweenConstructorParameters() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .assign(valueOf(MixedCtorPojo::getFinalCtorField)
                            .to(MixedCtorPojo::getNonFinalCtorField))
                    .create();

            assertThat(result.getNonFinalCtorField()).isEqualTo(result.getFinalCtorField());
        }

        @Test
        void betweenNonConstructorFields() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getWithoutSetter))
                    .create();

            assertThat(result.getWithoutSetter()).isEqualTo(result.getWithSetter());
        }

        /**
         * The constructor argument depends on a field of the same object
         * that is not a constructor parameter. The field's value is
         * generated before the constructor is invoked, and assigned
         * to the object afterwards.
         */
        @Test
        void nonConstructorFieldToConstructorParameter() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(result.getFinalCtorField()).isEqualTo(result.getWithSetter());
        }

        @Test
        void nonConstructorFieldWithoutSetterToConstructorParameter() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .assign(valueOf(MixedCtorPojo::getWithoutSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(result.getFinalCtorField()).isEqualTo(result.getWithoutSetter());
        }

        /**
         * Chain across both kinds of fields: a constructor argument
         * depends on a non-parameter field, which in turn depends
         * on a later-declared non-parameter field.
         */
        @Test
        void chainedNonConstructorFieldsToConstructorParameter() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .assign(valueOf(MixedCtorPojo::getWithoutSetter)
                            .to(MixedCtorPojo::getWithSetter))
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(result.getFinalCtorField())
                    .isEqualTo(result.getWithSetter())
                    .isEqualTo(result.getWithoutSetter());
        }

        @Test
        void conditionalOnNonConstructorField() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .set(field(MixedCtorPojo::getWithSetter), EXPECTED)
                    .assign(given(MixedCtorPojo::getWithSetter).satisfies(EXPECTED::equals)
                            .set(field(MixedCtorPojo::getFinalCtorField), "matched"))
                    .create();

            assertThat(result.getFinalCtorField()).isEqualTo("matched");
        }

        @Test
        void externalOriginCombinedWithNonParameterFields() {
            @SuppressWarnings("unused")
            class Wrapper {
                @Nullable MixedCtorPojo pojo; // depends on 'source', which is generated later
                @Nullable String source;
            }

            final Wrapper result = Instancio.of(Wrapper.class)
                    .assign(valueOf(field(Wrapper.class, "source"))
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            final MixedCtorPojo pojo = requireNonNull(result.pojo);

            assertThat(pojo.getFinalCtorField()).isEqualTo(result.source);
            assertThat(pojo.getWithSetter()).isNotBlank();
            assertThat(pojo.getWithoutSetter()).isNotBlank();
        }
    }

    @Nested
    class NonParameterFieldPreGenerationTest {

        @Test
        void ignoredNonParameterFieldIsSkipped() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .ignore(field(MixedCtorPojo::getWithoutSetter))
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(result.getFinalCtorField()).isEqualTo(result.getWithSetter());
            assertThat(result.getWithoutSetter()).isNull();
        }

        @Test
        void nonParameterFieldWithoutSetterIsSkippedUnderMethodAssignment() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                    .withSetting(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(result.getFinalCtorField()).isEqualTo(result.getWithSetter());
            assertThat(result.getWithoutSetter()).isNull();
        }

        @Test
        void cyclicNonParameterFieldIsSkipped() {
            final CyclicCtorPojo result = Instancio.of(CyclicCtorPojo.class)
                    .assign(valueOf(CyclicCtorPojo::getValue)
                            .to(CyclicCtorPojo::getId))
                    .create();

            assertThat(result.getId()).isEqualTo(result.getValue());
            assertThat(result.getSelf()).isNull();
        }
    }

    @Nested
    class ContainerElementsTest {

        @Test
        void listElementsAreAssignedIndependently() {
            final List<StringsAbcCtor> results = Instancio.ofList(StringsAbcCtor.class)
                    .size(Constants.SAMPLE_SIZE_D)
                    .assign(valueOf(StringsAbcCtor::getA).to(StringsAbcCtor::getB))
                    .create();

            assertThat(results).hasSize(Constants.SAMPLE_SIZE_D).allSatisfy(result ->
                    assertThat(result.getB()).isEqualTo(result.getA()));
        }

        @Test
        void viaStream() {
            final List<StringsAbcCtor> results = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsAbcCtor::getA).to(StringsAbcCtor::getB))
                    .stream()
                    .limit(5)
                    .toList();

            assertThat(results).hasSize(5).allSatisfy(result ->
                    assertThat(result.getB()).isEqualTo(result.getA()));
        }

        @Test
        void nonConstructorFieldOriginIsIsolatedPerElement() {
            final List<MixedCtorPojo> results = Instancio.ofList(MixedCtorPojo.class)
                    .size(Constants.SAMPLE_SIZE_D)
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(results).hasSize(Constants.SAMPLE_SIZE_D).allSatisfy(result ->
                    assertThat(result.getFinalCtorField()).isEqualTo(result.getWithSetter()));
        }
    }

    @Nested
    class WithModelTest {

        @Test
        void assignmentDefinedInModel() {
            final Model<StringsAbcCtor> model = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsAbcCtor::getA).to(StringsAbcCtor::getB))
                    .toModel();

            final StringsAbcCtor result = Instancio.create(model);

            assertThat(result.getB()).isEqualTo(result.getA());
        }
    }

    @Nested
    class WithOnCompleteTest {

        @Test
        void onCompleteInvokedForAssignedParameter() {
            final AtomicBoolean callbackInvoked = new AtomicBoolean();

            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(given(field(StringsAbcCtor::getA))
                            .satisfies(val -> true)
                            .generate(field(StringsAbcCtor::getB), gen -> gen.text().pattern(EXPECTED)))
                    .onComplete(field(StringsAbcCtor::getB), (String b) -> {
                        callbackInvoked.set(true);
                        assertThat(b).isEqualTo(EXPECTED);
                    })
                    .create();

            assertThat(callbackInvoked).isTrue();
            assertThat(result.getB()).isEqualTo(EXPECTED);
        }
    }

    @Nested
    class WithNullValuesTest {

        @Test
        void nullValueForConstructorParameter() {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(given(StringsAbcCtor::getA).satisfies(val -> true)
                            .set(field(StringsAbcCtor::getB), null))
                    .create();

            assertThat(result.getB()).isNull();
        }

        @Test
        void nullValueForNestedPojoParameter() {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .assign(given(StringsAbcCtor::getA).satisfies(val -> true)
                            .set(all(StringsDefCtor.class), null))
                    .create();

            assertThat(result.getDef()).isNull();
        }

        private static class WithPrimitiveParameter {
            private final int count;
            private final String label;

            WithPrimitiveParameter(final int count, final String label) {
                this.count = count;
                this.label = "CTOR:" + label;
            }
        }

        @Test
        void nullValueForDelayedPrimitiveParameter() {
            final WithPrimitiveParameter result = Instancio.of(WithPrimitiveParameter.class)
                    .assign(given(field(WithPrimitiveParameter.class, "label"))
                            .satisfies(val -> true)
                            .set(field(WithPrimitiveParameter.class, "count"), null))
                    .create();

            assertThat(result.label)
                    .as("The object should be created via constructor, without bypassing it")
                    .startsWith("CTOR:");
            assertThat(result.count).isZero();
        }
    }

    @Nested
    class AcrossInstantiationStrategiesTest {

        static Stream<Arguments> strategies() {
            return Stream.of(
                    // value-passing constructor preferred over the no-argument one
                    Arguments.of(InstantiationStrategies.of(
                            InstantiationStrategy.ALL_ARGS,
                            InstantiationStrategy.NO_ARGS,
                            InstantiationStrategy.BYPASS_CONSTRUCTOR)),
                    // the default order: no-argument constructor preferred
                    Arguments.of(InstantiationStrategies.of(
                            InstantiationStrategy.NO_ARGS,
                            InstantiationStrategy.ALL_ARGS,
                            InstantiationStrategy.BYPASS_CONSTRUCTOR)),
                    // instantiation via constructor disabled
                    Arguments.of(InstantiationStrategies.of(
                            InstantiationStrategy.NO_ARGS,
                            InstantiationStrategy.BYPASS_CONSTRUCTOR)));
        }

        @MethodSource("strategies")
        @ParameterizedTest
        void valueOfTo(final InstantiationStrategies strategies) {
            final StringsAbcCtor result = Instancio.of(StringsAbcCtor.class)
                    .withSetting(Keys.INSTANTIATION_STRATEGIES, strategies)
                    .assign(valueOf(StringsAbcCtor::getA).to(StringsAbcCtor::getB))
                    .create();

            assertThat(result.getB()).isEqualTo(result.getA());
        }

        @MethodSource("strategies")
        @ParameterizedTest
        void mixedFields(final InstantiationStrategies strategies) {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .withSetting(Keys.INSTANTIATION_STRATEGIES, strategies)
                    .assign(valueOf(MixedCtorPojo::getFinalCtorField)
                            .to(MixedCtorPojo::getWithSetter))
                    .create();

            assertThat(result.getWithSetter()).isEqualTo(result.getFinalCtorField());
        }

        @MethodSource("strategies")
        @ParameterizedTest
        void nonConstructorFieldToConstructorParameter(final InstantiationStrategies strategies) {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .withSetting(Keys.INSTANTIATION_STRATEGIES, strategies)
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(result.getFinalCtorField()).isEqualTo(result.getWithSetter());
        }
    }
}
