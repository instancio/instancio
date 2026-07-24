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
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;
import org.instancio.settings.Keys;
import org.instancio.settings.OnConstructorError;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.ALWAYS;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.CTOR_PREFIX;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.NEVER;

/**
 * NOTE: constructor parameter names are resolved from the
 * {@code LocalVariableTable} class file attribute, therefore these tests
 * rely on being compiled with debug information, which the Maven build
 * enables by default.
 */
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class InstantiationStrategyAdhocTest {

    @SuppressWarnings("unused")
    private static class Person {
        private final String firstName; // final and in constructor
        private String lastName;        // not final, but in constructor
        private @Nullable Integer age;  // not in constructor

        Person(final String firstName, final String lastName) {
            this.firstName = firstName;
            // canary: if the engine reassigns this field after
            // construction, the generated value loses the prefix
            this.lastName = CTOR_PREFIX + lastName;
        }

        @Nullable Integer getAge() {
            return age;
        }

        void setAge(final Integer age) {
            this.age = age;
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class CtorFlag {
        private static boolean ctorCalled;

        private final String name;

        CtorFlag(final String name) {
            ctorCalled = true;
            this.name = name;
        }
    }

    @SuppressWarnings({"unused", "ClassCanBeRecord"})
    private static class MultipleConstructors {
        private static String usedConstructor = "none";

        private final String a;
        private final @Nullable String b;

        MultipleConstructors(final String a) {
            this.a = a;
            this.b = null;
            usedConstructor = "one-arg";
        }

        MultipleConstructors(final String a, final String b) {
            this.a = a;
            this.b = b;
            usedConstructor = "two-arg";
        }
    }

    /**
     * Both constructors have the same number of parameters and both map to a
     * field, so the parameter count does not break the tie. The public one
     * wins, even though sorting by parameter type name alone would place
     * {@code Integer} before {@code String}.
     */
    @SuppressWarnings("unused")
    private static class TiedParameterCount {
        private static String usedConstructor = "none";

        private @Nullable String name;
        private @Nullable Integer age;

        public TiedParameterCount(final String name) {
            usedConstructor = "public";
            this.name = name;
        }

        TiedParameterCount(final Integer age) {
            usedConstructor = "package-private";
            this.age = age;
        }
    }

    /**
     * The setter has no corresponding field, so its child node has no field for
     * a constructor parameter to be matched against. The constructor parameter
     * shares the setter's name to ensure it is not matched to that node.
     */
    @SuppressWarnings("unused")
    private static class UnmatchedSetter {
        private @Nullable String name;

        UnmatchedSetter(final String ghost) {
            throw new AssertionError("Constructor with an unmapped parameter should not be invoked");
        }

        public void setName(final String name) {
            this.name = name;
        }

        public void setGhost(final String ghost) {
            // no corresponding field
        }
    }

    /**
     * The constructor parameter cannot be mapped to a field.
     */
    @SuppressWarnings("ClassCanBeRecord")
    private static class UnmappedParameter {
        private final int length;

        UnmappedParameter(final String source) {
            this.length = source.length();
        }
    }

    /**
     * Neither constructor's parameters can be mapped to a field, so neither
     * is a candidate for instantiation, no matter how many parameters it has.
     */
    @SuppressWarnings("unused")
    private static class MultipleUnmappedParameters {
        private static String usedConstructor = "none";

        private final int length;

        MultipleUnmappedParameters(final String source) {
            usedConstructor = "one-arg";
            this.length = source.length();
        }

        MultipleUnmappedParameters(final String source, final String other) {
            usedConstructor = "two-arg";
            this.length = source.length() + other.length();
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    private static class ValidatingConstructor {
        private final String name;

        ValidatingConstructor(final String name) {
            if (name == null || name.length() < 1000) { // never satisfied by generated values
                throw new IllegalArgumentException("invalid name: " + name);
            }
            this.name = name;
        }
    }

    @SuppressWarnings("unused")
    private static class WithBuilder {
        private @Nullable String name;

        WithBuilder() {
        }

        WithBuilder(final Builder builder) {
            throw new AssertionError("Builder constructor should not be invoked");
        }

        private static class Builder {
        }
    }

    @SuppressWarnings("unused")
    private static class CopyConstructorOnly {
        private @Nullable String name;

        CopyConstructorOnly() {
        }

        CopyConstructorOnly(final CopyConstructorOnly other) {
            throw new AssertionError("Copy constructor should not be invoked");
        }
    }

    private record PersonRecord(String name, int age) {}

    private record RawPair<T>(String name, T value) {}

    @SuppressWarnings({"unused", "rawtypes"})
    private static class RawPairHolder {
        // the component 'T value' cannot be resolved for a raw type,
        // so the record's constructor cannot be resolved either
        private @Nullable RawPair rawPair;
    }

    @SuppressWarnings("unused")
    private static class NoArgsOnly {
        private @Nullable String name;
    }

    private record Inner(@Nullable String a, int b) {}

    @SuppressWarnings("unused")
    private static class InnerHolder {
        private @Nullable Inner inner;
    }

    /**
     * The unmapped constructor parameter is a POJO. Instancio must not
     * generate a subtree for it: the parameter maps to no field, so the
     * constructor is not a candidate at all.
     */
    @SuppressWarnings("ClassCanBeRecord")
    private static class PojoParameter {
        private final int total;

        PojoParameter(final IntPair pair) {
            throw new AssertionError("Constructor with an unmapped parameter should not be invoked");
        }
    }

    @SuppressWarnings("unused")
    private static class IntPair {
        private int a;
        private int b;
    }

    /**
     * Primitive field matched to a wrapper-type constructor parameter.
     */
    @SuppressWarnings("ClassCanBeRecord")
    private static class BoxedParameter {
        private final int count;

        BoxedParameter(final Integer count) {
            this.count = count;
        }
    }

    @Test
    void constructorIsInvoked() {
        CtorFlag.ctorCalled = false;

        final CtorFlag result = Instancio.create(CtorFlag.class);

        assertThat(CtorFlag.ctorCalled).isTrue();
        assertThat(result.name).isNotBlank();
    }

    @Test
    void constructorWithMostParametersIsPicked() {
        MultipleConstructors.usedConstructor = "none";

        final MultipleConstructors result = Instancio.create(MultipleConstructors.class);

        assertThat(MultipleConstructors.usedConstructor).isEqualTo("two-arg");
        assertThat(result.a).isNotBlank();
        assertThat(result.b).isNotBlank();
    }

    @Test
    void publicConstructorIsPickedWhenParameterCountIsTied() {
        TiedParameterCount.usedConstructor = "none";

        final TiedParameterCount result = Instancio.of(TiedParameterCount.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .create();

        assertThat(TiedParameterCount.usedConstructor).isEqualTo("public");
        assertThat(result.name).isNotBlank();
    }

    /**
     * A setter without a corresponding field produces a child node with no
     * field, which no constructor parameter can be matched to.
     */
    @Test
    void parameterIsNotMatchedToASetterWithoutAField() {
        final UnmatchedSetter result = Instancio.of(UnmatchedSetter.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .withSetting(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
                .create();

        assertThat(result.name)
                .as("Should be assigned via the setter after the constructor was rejected")
                .isNotBlank();
    }

    @Test
    void neverMode_valuesAreAssignedDirectlyToFields() {
        // NOTE: the constructor may still be invoked in NEVER mode
        // (with default argument values) if instantiation without
        // a constructor is not possible on the current JVM
        final Person result = Instancio.of(Person.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, NEVER)
                .create();

        assertThat(result.firstName).isNotBlank();
        assertThat(result.lastName)
                .as("Generated value should be assigned directly to the field")
                .doesNotStartWith(CTOR_PREFIX);
        assertThat(result.age).isNotNull();
    }

    /**
     * A constructor with an unmapped parameter is never invoked, even when
     * every strategy is enabled: the value generated for such a parameter
     * could not be reached by a selector. The field is assigned directly
     * instead, so the selector applies.
     */
    @Test
    void alwaysMode_unmappedParameterFallsBackToFieldAssignment() {
        final UnmappedParameter result = Instancio.of(UnmappedParameter.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .set(field(UnmappedParameter.class, "length"), 42)
                .create();

        assertThat(result.length).isEqualTo(42);
    }

    @Test
    void alwaysMode_noCandidateIsUsedWhenAllParametersAreUnmapped() {
        MultipleUnmappedParameters.usedConstructor = "none";

        final MultipleUnmappedParameters result = Instancio.of(MultipleUnmappedParameters.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .set(field(MultipleUnmappedParameters.class, "length"), 42)
                .create();

        assertThat(MultipleUnmappedParameters.usedConstructor)
                .as("Neither constructor should be invoked")
                .isEqualTo("none");

        assertThat(result.length).isEqualTo(42);
    }

    @Test
    void autoMode_unmappedParameterFallsBackToFieldAssignment() {
        final UnmappedParameter result = Instancio.create(UnmappedParameter.class);

        // the int field is assigned directly; generated ints are positive by default
        assertThat(result.length).isPositive();
    }

    @Test
    void constructorError_fallsBackByDefault() {
        final ValidatingConstructor result = Instancio.create(ValidatingConstructor.class);

        assertThat(result).isNotNull();
        assertThat(result.name)
                .as("Fallback should populate fields directly")
                .isNotBlank();
    }

    @Test
    void constructorError_failSetting() {
        final InstancioApi<ValidatingConstructor> api = Instancio.of(ValidatingConstructor.class)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed instantiating an object via constructor");
    }

    @SuppressWarnings("unused")
    private static class ThrowingAllArgsWithWorkingNoArgs {
        private @Nullable String name;

        ThrowingAllArgsWithWorkingNoArgs() {
        }

        ThrowingAllArgsWithWorkingNoArgs(final String name) {
            throw new IllegalStateException("all-args constructor rejected");
        }
    }

    @Test
    void constructorError_fallbackDoesNotResumeTheStrategyList() {
        // FALLBACK bypasses the failed constructor rather than continuing
        // with the next strategy, so the working no-argument constructor is
        // NOT attempted; without BYPASS_CONSTRUCTOR there is no way to
        // allocate the object, hence a null result
        final ThrowingAllArgsWithWorkingNoArgs result = Instancio.of(ThrowingAllArgsWithWorkingNoArgs.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(
                        InstantiationStrategy.ALL_ARGS, InstantiationStrategy.NO_ARGS))
                .create();

        assertThat(result).isNull();
    }

    @Test
    void constructorError_fallbackAllocatesWithoutConstructorWhenBypassIsListed() {
        final ThrowingAllArgsWithWorkingNoArgs result = Instancio.of(ThrowingAllArgsWithWorkingNoArgs.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, InstantiationStrategies.of(
                        InstantiationStrategy.ALL_ARGS, InstantiationStrategy.BYPASS_CONSTRUCTOR))
                .create();

        assertThat(result).isNotNull();
        assertThat(result.name).isNotBlank();
    }

    /**
     * NOTE: {@link ConstructorTestSupport#ALWAYS} is required. With the default
     * strategy order the no-argument constructor is resolved first, so the
     * builder constructor is never even considered as a candidate.
     */
    @Test
    void builderConstructorIsExcluded() {
        final WithBuilder result = Instancio.of(WithBuilder.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .create();

        assertThat(result.name).isNotBlank();
    }

    /**
     * NOTE: {@link ConstructorTestSupport#ALWAYS} is required, as described
     * in {@link #builderConstructorIsExcluded()}.
     */
    @Test
    void copyConstructorIsExcluded() {
        // If the copy constructor were treated as a candidate, the engine
        // would try to generate a CopyConstructorOnly for its parameter;
        // instead the class is instantiated via the no-arg constructor
        // (the copy constructor throws if ever invoked) and its field populated.
        final CopyConstructorOnly result = Instancio.of(CopyConstructorOnly.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .create();

        assertThat(result.name).isNotBlank();
    }

    @Test
    void recordWithUnresolvableComponentResultsInNullValue() {
        final RawPairHolder result = Instancio.create(RawPairHolder.class);

        assertThat(result.rawPair)
                .as("A record whose constructor cannot be resolved should be null")
                .isNull();
    }

    @Test
    void recordWithUnresolvableComponentCanBeSuppliedByUser() {
        final RawPair<Integer> suppliedValue = new RawPair<>("supplied", 123);

        final RawPairHolder result = Instancio.of(RawPairHolder.class)
                .supply(all(RawPair.class), () -> suppliedValue)
                .create();

        assertThat(result.rawPair).isSameAs(suppliedValue);
    }

    @Test
    void recordAtMaxDepthIsCreatedWithoutConstructor() {
        final InnerHolder result = Instancio.of(InnerHolder.class)
                .withMaxDepth(1)
                .create();

        // the record node has no children at max depth,
        // so a blank instance is created without a constructor
        assertThat(result.inner).isNotNull();

        final Inner inner = requireNonNull(result.inner);
        assertThat(inner.a()).isNull();
        assertThat(inner.b()).isZero();
    }

    @Test
    void alwaysMode_classWithoutCandidateConstructors() {
        final NoArgsOnly result = Instancio.of(NoArgsOnly.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .create();

        assertThat(result.name).isNotBlank();
    }

    /**
     * The constructor throws if invoked, so this also guards against
     * generating a subtree for the unmapped POJO parameter.
     */
    @Test
    void alwaysMode_unmappedPojoParameterConstructorIsNotUsed() {
        final PojoParameter result = Instancio.of(PojoParameter.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .set(field(PojoParameter.class, "total"), 42)
                .create();

        assertThat(result.total).isEqualTo(42);
    }

    @Test
    void primitiveFieldMatchesWrapperParameter() {
        final BoxedParameter result = Instancio.create(BoxedParameter.class);

        assertThat(result.count).isPositive();
    }

    @Test
    void recordsAreCreatedViaConstructorRegardlessOfMode() {
        final PersonRecord result = Instancio.of(PersonRecord.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, NEVER)
                .create();

        assertThat(result.name()).isNotBlank();
        assertThat(result.age()).isPositive();
    }

    // NOTE: must be top-level/static class for constructor to be resolved
    static class WithDefaultConstructor {
        private int value;
        private boolean constructorInvoked;

        private WithDefaultConstructor() {
            constructorInvoked = true;
        }
    }

    @Test
    void neverModeWithDefaultConstructor() {
        final WithDefaultConstructor result = Instancio.of(WithDefaultConstructor.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, NEVER)
                .ignore(field("constructorInvoked"))
                .create();

        assertThat(result.constructorInvoked).isTrue();
        assertThat(result.value).is(Conditions.RANDOM_INTEGER);
    }

    @SuppressWarnings("unused")
    static class ValidatingNoArgsConstructor {
        private @Nullable String name;

        ValidatingNoArgsConstructor() {
            throw new IllegalStateException("no-args constructor rejected");
        }
    }

    @Test
    void noArgsConstructorError_fallsBackByDefault() {
        // the throwing no-arg constructor is bypassed and the object is
        // allocated without a constructor, then populated via fields
        final ValidatingNoArgsConstructor result = Instancio.of(ValidatingNoArgsConstructor.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, NEVER)
                .create();

        assertThat(result.name).isNotBlank();
    }

    @Test
    void noArgsConstructorError_failSetting() {
        final InstancioApi<ValidatingNoArgsConstructor> api = Instancio.of(ValidatingNoArgsConstructor.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, NEVER)
                .withSetting(Keys.ON_CONSTRUCTOR_ERROR, OnConstructorError.FAIL);

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("failed instantiating")
                .hasMessageContaining("ValidatingNoArgsConstructor()");
    }

    /**
     * Automatic back-references ({@link Keys#SET_BACK_REFERENCES}) require the
     * owning object to be registered as a back-reference target before its
     * descendants are generated. An object created via the normal constructor
     * path is registered; one created via the constructor-error fallback path
     * is not, so a descendant's back-reference to it is silently left null.
     */
    @Nested
    class ConstructorErrorFallbackBackReferenceTest {

        private static class BackRefChild {
            @Nullable BackRefParent parent;
        }

        private static class BackRefParent {
            @Nullable BackRefChild child;

            private BackRefParent() {
                throw new IllegalStateException("no-args constructor rejected");
            }
        }

        private static class OkParent {
            @Nullable OkChild child;
        }

        private static class OkChild {
            @Nullable OkParent parent;
        }

        @Test
        void normalPath_registersObjectAsBackReferenceTarget() {
            final OkParent result = Instancio.of(OkParent.class)
                    .withSetting(Keys.SET_BACK_REFERENCES, true)
                    .create();

            assertThat(requireNonNull(result.child).parent)
                    .as("object created via constructor is a back-reference target")
                    .isSameAs(result);
        }

        @Disabled("Known limitation: an object created via the constructor-error "
                + "fallback path (InstancioEngine.generateViaInstantiator) is not "
                + "registered as a back-reference target, unlike the normal path")
        @Test
        void fallbackPath_shouldRegisterObjectAsBackReferenceTarget() {
            // The no-args constructor throws, so the default ON_CONSTRUCTOR_ERROR=FALLBACK
            // allocates the object without a constructor (BYPASS_CONSTRUCTOR is a default strategy)
            final BackRefParent result = Instancio.of(BackRefParent.class)
                    .withSetting(Keys.SET_BACK_REFERENCES, true)
                    .create();

            assertThat(requireNonNull(result.child).parent).isSameAs(result);
        }
    }
}
