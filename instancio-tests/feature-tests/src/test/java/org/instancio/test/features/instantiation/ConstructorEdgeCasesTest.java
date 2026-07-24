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
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.ALWAYS;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.CTOR_PREFIX;
import static org.instancio.test.features.instantiation.ConstructorTestSupport.SETTER_PREFIX;

// NOTE: classes are intentionally not records to verify handling of POJOs.
// Some fields are also intentionally not final.
@SuppressWarnings({"ClassCanBeRecord", "FieldMayBeFinal"})
@FeatureTag(Feature.INSTANTIATION_STRATEGIES)
@ExtendWith(InstancioExtension.class)
class ConstructorEdgeCasesTest {

    @SuppressWarnings("unused")
    private static class PersonPojo {
        private final @Nullable String firstName;
        private String lastName;
        private @Nullable String nickName; // not in constructor; populated via setter

        PersonPojo(@Nullable final String firstName, final String lastName) {
            this.firstName = firstName;
            this.lastName = CTOR_PREFIX + lastName;
        }

        void setNickName(@Nullable final String nickName) {
            this.nickName = SETTER_PREFIX + nickName;
        }
    }

    private static class SelfReference {
        private final @Nullable SelfReference next;
        private final String name;

        SelfReference(final @Nullable SelfReference next, final String name) {
            this.next = next;
            this.name = name;
        }
    }

    private static class InitialisingConstructor {
        private final String name;
        private String status; // not a parameter; initialised by the constructor

        InitialisingConstructor(final String name) {
            this.name = name;
            this.status = "NEW";
        }
    }

    private static class GenericHolder<T> {
        private final T value;

        GenericHolder(final T value) {
            this.value = value;
        }
    }

    /**
     * The unmapped parameter's type is a type variable declared by the
     * constructor itself, therefore it cannot be resolved to a type,
     * and no node can be created for the parameter.
     */
    @SuppressWarnings("unused")
    private static class ConstructorTypeVariable {
        private String name;

        <T> ConstructorTypeVariable(final String name, final T unmapped) {
            this.name = CTOR_PREFIX + name;
        }
    }

    /**
     * A non-static member class: its constructor has an implicit parameter
     * for the enclosing instance, which javac backs with a synthetic
     * {@code this$0} field. The parameter would therefore map to a field,
     * and the constructor would be invoked with a generated enclosing
     * instance, if such classes were not excluded outright.
     *
     * <p>NOTE: intentionally not {@code private}, since parameter names
     * of a private inner class do not resolve, which would mask the
     * exclusion this test is verifying.
     */
    @SuppressWarnings("all")
    class NonStaticMemberClass {
        private String value;

        NonStaticMemberClass(final String value) {
            this.value = CTOR_PREFIX + value;
        }
    }

    /**
     * The superclass of an anonymous class. An anonymous class declares a
     * constructor mirroring the one invoked by its declaration, so its
     * parameters would map to inherited fields; it may also have implicit
     * parameters for the enclosing instance and any captured variables.
     */
    static class AnonymousBase {
        private String name;

        AnonymousBase(final String name) {
            this.name = CTOR_PREFIX + name;
        }
    }

    @SuppressWarnings("all")
    private static final Class<? extends AnonymousBase> ANONYMOUS_CLASS =
            new AnonymousBase("seed") {}.getClass();

    @Test
    void constructorPojoInCollections() {
        final List<PersonPojo> list = Instancio.ofList(PersonPojo.class).size(3).create();

        assertThat(list).hasSize(3).allSatisfy(person -> {
            assertThat(person.firstName).is(Conditions.RANDOM_STRING);
            assertThat(person.lastName).startsWith(CTOR_PREFIX);
            assertThat(person.nickName).isNotNull();
        });
    }

    @Test
    void constructorPojoAsMapValue() {
        final Map<String, PersonPojo> map = Instancio.ofMap(String.class, PersonPojo.class).size(2).create();

        assertThat(map).values()
                .hasSize(2)
                .allMatch(person -> person.lastName.startsWith(CTOR_PREFIX));
    }

    @Test
    void constructorPojoInArray() {
        final PersonPojo[] array = Instancio.create(PersonPojo[].class);

        assertThat(array)
                .isNotEmpty()
                .allMatch(person -> person.lastName.startsWith(CTOR_PREFIX));
    }

    @Test
    void constructorPojoViaStream() {
        final List<PersonPojo> results = Instancio.of(PersonPojo.class).stream()
                .limit(5)
                .toList();

        assertThat(results)
                .hasSize(5)
                .allMatch(person -> person.lastName.startsWith(CTOR_PREFIX));
    }

    @Test
    void cyclicConstructorParameter() {
        final SelfReference result = Instancio.create(SelfReference.class);

        assertThat(result.name).is(Conditions.RANDOM_STRING);
        assertThat(result.next).as("cycle must terminate").isNull();
    }

    @Test
    void maxDepthReached() {
        class Holder {
            @Nullable PersonPojo person;
        }

        final Holder result = Instancio.of(Holder.class)
                .withMaxDepth(1)
                .create();

        // Person node has no children at max depth,
        // so it is instantiated without a constructor
        final PersonPojo person = requireNonNull(result.person);
        assertThat(person.firstName).isNull();
        assertThat(person.nickName).isNull();
    }

    @Test
    void methodAssignment() {
        final PersonPojo result = Instancio.of(PersonPojo.class)
                .withSetting(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                .create();

        assertThat(result.firstName).is(Conditions.RANDOM_STRING);
        assertThat(result.lastName).startsWith(CTOR_PREFIX);
        assertThat(result.nickName).startsWith(SETTER_PREFIX);
    }

    @Test
    void overwriteExistingValuesDisabled_preservesValuesSetByConstructorBody() {
        final InitialisingConstructor result = Instancio.of(InitialisingConstructor.class)
                .withSetting(Keys.OVERWRITE_EXISTING_VALUES, false)
                .create();

        assertThat(result.name).is(Conditions.RANDOM_STRING);
        assertThat(result.status).isEqualTo("NEW");
    }

    @Test
    void overwriteExistingValuesEnabled_overwritesValuesSetByConstructorBody() {
        final InitialisingConstructor result = Instancio.create(InitialisingConstructor.class);

        assertThat(result.name).is(Conditions.RANDOM_STRING);
        assertThat(result.status).isNotEqualTo("NEW");
    }

    @Test
    void genericConstructorParameter() {
        final GenericHolder<PersonPojo> result = Instancio.create(new TypeToken<>() {});

        assertThat(result.value).isNotNull();
        assertThat(result.value.lastName).startsWith(CTOR_PREFIX);
    }

    @Test
    void alwaysMode_unmappedParameterWithUnresolvableType() {
        final ConstructorTypeVariable result = Instancio.of(ConstructorTypeVariable.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .create();

        assertThat(result.name)
                .as("No node can be created for the unmapped parameter, so the class "
                        + "is instantiated without a constructor and fields assigned directly")
                .is(Conditions.RANDOM_STRING);
    }

    @Test
    void alwaysMode_anonymousClassIsNotEligibleForConstructor() {
        assertThat(ANONYMOUS_CLASS.isAnonymousClass()).isTrue();

        final AnonymousBase result = Instancio.of(ANONYMOUS_CLASS)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .create();

        assertThat(result.name)
                .as("Anonymous classes are excluded, so the class "
                        + "is instantiated without a constructor")
                .is(Conditions.RANDOM_STRING);
    }

    @Test
    void alwaysMode_nonStaticMemberClassIsNotEligibleForConstructor() {
        final NonStaticMemberClass result = Instancio.of(NonStaticMemberClass.class)
                .withSetting(Keys.INSTANTIATION_STRATEGIES, ALWAYS)
                .create();

        assertThat(result.value)
                .as("Constructor takes an implicit enclosing instance parameter, "
                        + "so the class is instantiated without a constructor")
                .is(Conditions.RANDOM_STRING);
    }

    /**
     * Superclass fields populated through a {@code super(...)} call, and
     * inherited fields that are not constructor parameters populated as
     * usual afterwards.
     */
    @Nested
    class InheritanceTest {

        private static class Base {
            final String baseName; // set via super(...); package-private for subclass-ref access

            private Base(final String baseName) {
                this.baseName = CTOR_PREFIX + baseName;
            }
        }

        @SuppressWarnings("unused")
        private static final class Sub extends Base {
            private final String subName;  // set via this constructor
            private @Nullable String note; // not a parameter; populated afterwards

            private Sub(final String baseName, final String subName) {
                super(baseName);
                this.subName = CTOR_PREFIX + subName;
            }

            void setNote(@Nullable final String note) {
                this.note = note;
            }
        }

        @Test
        void superclassFieldViaSuperConstructor() {
            final Sub result = Instancio.create(Sub.class);

            assertThat(result.baseName)
                    .as("superclass field routed through super(...)")
                    .startsWith(CTOR_PREFIX);

            assertThat(result.subName).startsWith(CTOR_PREFIX);
        }

        @Test
        void inheritedNonConstructorFieldPopulatedAfterward() {
            final Sub result = Instancio.create(Sub.class);

            assertThat(result.note)
                    .as("field that is not a constructor parameter is populated afterwards")
                    .is(Conditions.RANDOM_STRING);
        }
    }

    /**
     * A subclass field that shadows a same-named superclass field.
     * The single constructor parameter is matched to the subclass field.
     */
    @Nested
    class ShadowedFieldTest {

        @SuppressWarnings("unused")
        private static class ShadowBase {
            String value; // shadowed by the subclass field of the same name

            private ShadowBase(final String value) {
                this.value = ShadowBase.class + value;
            }
        }

        @SuppressWarnings("all")
        private static final class ShadowSub extends ShadowBase {
            private final String value; // shadows ShadowBase.value intentionally

            private ShadowSub(final String value) {
                super(value);
                this.value = ShadowSub.class + value;
            }
        }

        @Test
        void constructorArgumentIsBoundToTheSubclassField() {
            final ShadowSub result = Instancio.create(ShadowSub.class);

            assertThat(result.value).startsWith(ShadowSub.class.toString());
            assertThat(((ShadowBase) result).value).doesNotStartWith("class ");
        }

        @Test
        void selectorOnSubclassFieldFlowsIntoConstructorArgument() {
            final ShadowSub result = Instancio.of(ShadowSub.class)
                    .set(field(ShadowSub.class, "value"), "X")
                    .create();

            assertThat(result.value).isEqualTo(ShadowSub.class + "X");
        }

        @Test
        void selectorOnSuperclassFieldDoesNotReachConstructor() {
            final ShadowSub result = Instancio.of(ShadowSub.class)
                    .set(field(ShadowBase.class, "value"), "X")
                    .create();

            assertThat(((ShadowBase) result).value).isEqualTo("X");
            assertThat(result.value).startsWith(ShadowSub.class.toString());
        }
    }
}
