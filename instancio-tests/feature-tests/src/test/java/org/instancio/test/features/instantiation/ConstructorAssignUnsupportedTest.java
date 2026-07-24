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

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;
import org.instancio.settings.Keys;
import org.instancio.test.support.pojo.constructor.ChildCtorPojo;
import org.instancio.test.support.pojo.constructor.MixedCtorPojo;
import org.instancio.test.support.pojo.constructor.ParentCtorPojo;
import org.instancio.test.support.pojo.constructor.StringsAbcCtor;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

/**
 * Assignments that cannot be resolved when the target object
 * is instantiated via constructor, resulting in an
 * {@link UnresolvedAssignmentException} &mdash; the same behaviour
 * as records ({@code AssignUnsupportedRecordsTest}).
 *
 * <p>Where the equivalent assignment is supported without constructor
 * instantiation, a contrast test verifies it with instantiation via
 * constructor disabled (the {@link #NO_CONSTRUCTOR} strategies).
 */
@FeatureTag({Feature.ASSIGN, Feature.INSTANTIATION_STRATEGIES, Feature.UNSUPPORTED})
@ExtendWith(InstancioExtension.class)
class ConstructorAssignUnsupportedTest {

    private static final InstantiationStrategies NO_CONSTRUCTOR = InstantiationStrategies.of(
            InstantiationStrategy.NO_ARGS, InstantiationStrategy.BYPASS_CONSTRUCTOR);

    @Nested
    class CyclicAssignmentTest {

        /**
         * Applies regardless of how the object is instantiated:
         * mutually dependent assignments can never be resolved.
         */
        @Test
        void mutualAssignmentBetweenConstructorParameters() {
            final InstancioApi<StringsAbcCtor> api = Instancio.of(StringsAbcCtor.class)
                    .assign(valueOf(StringsAbcCtor::getA).to(StringsAbcCtor::getB))
                    .assign(valueOf(StringsAbcCtor::getB).to(StringsAbcCtor::getA));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        void mutualAssignmentBetweenConstructorParameterAndFieldOfEnclosingPojo() {
            @Data
            class CtorPojoAndFieldRoot {
                @Nullable StringsAbcCtor ctorPojo;
                @Nullable String rootField;
            }

            final InstancioApi<CtorPojoAndFieldRoot> api = Instancio.of(CtorPojoAndFieldRoot.class)
                    .assign(valueOf(StringsAbcCtor::getA).to(field(CtorPojoAndFieldRoot::getRootField)))
                    .assign(valueOf(field(CtorPojoAndFieldRoot::getRootField)).to(StringsAbcCtor::getA));

            assertUnresolvedAssignmentException(api);
        }
    }

    @Nested
    class WithoutOverwriteExistingValuesTest {

        /**
         * A constructor argument depending on a non-parameter field
         * is normally resolved by generating the field's value before
         * invoking the constructor. With overwrite disabled, whether
         * the field's value should be generated at all depends on the
         * state of the constructed object, which does not exist yet,
         * so the assignment cannot be resolved.
         */
        @Test
        void constructorParameterFromNonConstructorField() {
            final InstancioApi<MixedCtorPojo> api = Instancio.of(MixedCtorPojo.class)
                    .withSetting(Keys.OVERWRITE_EXISTING_VALUES, false)
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        void sameAssignmentWorksWithoutConstructor() {
            final MixedCtorPojo result = Instancio.of(MixedCtorPojo.class)
                    .withSetting(Keys.OVERWRITE_EXISTING_VALUES, false)
                    .withSetting(Keys.INSTANTIATION_STRATEGIES, NO_CONSTRUCTOR)
                    .assign(valueOf(MixedCtorPojo::getWithSetter)
                            .to(MixedCtorPojo::getFinalCtorField))
                    .create();

            assertThat(result.getFinalCtorField())
                    .isNotBlank()
                    .isEqualTo(result.getWithSetter());
        }
    }

    @Nested
    class BackReferenceViaConstructorParameterTest {

        /**
         * The parent cannot be constructed until its children are,
         * and each child requires a reference to the parent as
         * a constructor argument &mdash; a circular dependency.
         */
        @Test
        void backReferenceToRoot() {
            final InstancioApi<ParentCtorPojo> api = Instancio.of(ParentCtorPojo.class)
                    .assign(valueOf(root()).to(ChildCtorPojo::getParent));

            assertUnresolvedAssignmentException(api);
        }

        @Test
        void sameAssignmentWorksWithoutConstructor() {
            final ParentCtorPojo result = Instancio.of(ParentCtorPojo.class)
                    .withSetting(Keys.INSTANTIATION_STRATEGIES, NO_CONSTRUCTOR)
                    .assign(valueOf(root()).to(ChildCtorPojo::getParent))
                    .create();

            assertThat(result.getChildren())
                    .isNotEmpty()
                    .allSatisfy(child -> assertThat(child.getParent()).isSameAs(result));
        }
    }

    private static void assertUnresolvedAssignmentException(final InstancioApi<?> api) {
        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(UnresolvedAssignmentException.class)
                .hasMessageContaining("unresolved assignment");
    }
}
