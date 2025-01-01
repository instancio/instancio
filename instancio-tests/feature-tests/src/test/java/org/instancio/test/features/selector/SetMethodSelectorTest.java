/*
 * Copyright 2022-2025 the original author or authors.
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
import org.instancio.InstancioApi;
import org.instancio.SetMethodSelector;
import org.instancio.TypeToken;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance.BaseClass;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance.SubClass;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.pojo.misc.getters.PropertyStylePojo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.setter;
import static org.instancio.test.support.asserts.Asserts.assertAllNulls;

@RunWith.MethodAssignmentOnly
@FeatureTag({Feature.ASSIGNMENT_TYPE_METHOD, Feature.METHOD_REFERENCE_SELECTOR})
@ExtendWith(InstancioExtension.class)
class SetMethodSelectorTest {

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL);

    @Test
    void selectorWithMethodNameOnlyShouldApplyToRootClass() {
        final Person result = Instancio.of(Person.class)
                .set(setter("setName"), "foo")
                .create();

        assertThat(result.getName()).isEqualTo("foo");
    }

    @Test
    void simpleSetters() {
        final Person expected = Instancio.create(Person.class);

        final Person result = Instancio.of(Person.class)
                .set(setter(Person::setUuid), expected.getUuid())
                .set(setter(Person::setUuid), expected.getUuid())
                .set(setter(Person::setName), expected.getName())
                .set(setter(Person::setAge), expected.getAge())
                .set(setter(Person::setGender), expected.getGender())
                .set(setter(Person::setLastModified), expected.getLastModified())
                .set(setter(Person::setPets), expected.getPets())
                .set(setter(Address::setCity), expected.getAddress().getCity())
                .set(setter(Address::setPhoneNumbers), expected.getAddress().getPhoneNumbers())
                .create();

        assertThat(result.getUuid()).isEqualTo(expected.getUuid());
        assertThat(result.getName()).isEqualTo(expected.getName());
        assertThat(result.getAge()).isEqualTo(expected.getAge());
        assertThat(result.getGender()).isEqualTo(expected.getGender());
        assertThat(result.getLastModified()).isEqualTo(expected.getLastModified());
        assertThat(result.getPets()).isSameAs(expected.getPets());
        assertThat(result.getAddress().getCity()).isEqualTo(expected.getAddress().getCity());
        assertThat(result.getAddress().getPhoneNumbers()).isSameAs(expected.getAddress().getPhoneNumbers());
    }

    @Test
    void passingMethodSelectorDirectly() {
        final SetMethodSelector<Phone, String> setNumber = Phone::setNumber;
        final Phone result = Instancio.of(Phone.class)
                // passing method selector directly without wrapping as "method(setNumber)"
                .set(setNumber, "foo")
                .create();

        assertThat(result.getNumber()).isEqualTo("foo");
    }

    /**
     * For generic return types, the type must be specified explicitly.
     */
    @Nested
    class MethodWithGenericParameterTypeTest {

        @WithSettings
        private final Settings settings = SETTINGS;

        @Test
        void genericItem() {
            final Item<String> result = Instancio.of(new TypeToken<Item<String>>() {})
                    .set(setter(Item<String>::setValue), "foo")
                    .create();

            assertThat(result.getValue()).isEqualTo("foo");
        }

        @Test
        void genericPair() {
            final SetMethodSelector<Pair<String, Integer>, String> setLeftMethod = Pair::setLeft;
            final SetMethodSelector<Pair<String, Integer>, Integer> setRightMethod = Pair::setRight;

            final Pair<String, Integer> result = Instancio.of(new TypeToken<Pair<String, Integer>>() {})
                    .set(setLeftMethod, "foo")
                    .set(setRightMethod, 123)
                    .create();

            assertThat(result.getLeft()).isEqualTo("foo");
            assertThat(result.getRight()).isEqualTo(123);
        }
    }

    @Test
    void propertyStyleSetter() {
        final SetMethodSelector<PropertyStylePojo, String> foo = PropertyStylePojo::foo;
        final SetMethodSelector<PropertyStylePojo, Integer> bar = PropertyStylePojo::bar;
        final SetMethodSelector<PropertyStylePojo, Boolean> isBaz = PropertyStylePojo::isBaz;
        final SetMethodSelector<PropertyStylePojo, Boolean> haz = PropertyStylePojo::haz;
        final SetMethodSelector<PropertyStylePojo, Boolean> hasGaz = PropertyStylePojo::hasGaz;

        final PropertyStylePojo expected = Instancio.of(PropertyStylePojo.class)
                .withSetting(Keys.SETTER_STYLE, SetterStyle.PROPERTY)
                .create();

        final PropertyStylePojo result = Instancio.of(PropertyStylePojo.class)
                .withSetting(Keys.SETTER_STYLE, SetterStyle.PROPERTY)
                .set(foo, expected.foo())
                .set(setter(bar), expected.bar())
                .set(setter(isBaz), expected.isBaz())
                .set(setter(haz), expected.haz())
                .set(setter(hasGaz), expected.hasGaz())
                .create();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void group() {
        final Person result = Instancio.of(Person.class)
                .ignore(all(
                        setter(Person::setUuid),
                        setter(Person::setGender),
                        setter(Person::setLastModified)))
                .create();

        assertAllNulls(result.getUuid(), result.getGender(), result.getLastModified());
    }

    @Test
    void withinScope() {
        final RichPerson result = Instancio.of(RichPerson.class)
                .set(setter(Address::setCity).within(setter(RichPerson::setAddress1).toScope()), "foo")
                .set(setter(Address::setCity).within(setter(RichPerson::setAddress2).toScope()), "bar")
                .create();

        assertThat(result.getAddress1().getCity()).isEqualTo("foo");
        assertThat(result.getAddress2().getCity()).isEqualTo("bar");
    }

    @Nested
    class InheritanceTest {
        @WithSettings
        private final Settings settings = SETTINGS;

        @Test
        void usingSubclassSetters() {
            final SubClass expected = Instancio.create(SubClass.class);

            final SubClass result = Instancio.of(SubClass.class)
                    .set(setter(SubClass::setSubClassField), expected.getSubClassField())
                    .set(setter(SubClass::setProtectedBaseClassField), expected.getProtectedBaseClassField())
                    .set(setter(SubClass::setPrivateBaseClassField), expected.getPrivateBaseClassField())
                    .create();

            assertThat(result.getSubClassField()).isEqualTo(expected.getSubClassField());
            assertThat(result.getProtectedBaseClassField()).isEqualTo(expected.getProtectedBaseClassField());
            assertThat(result.getPrivateBaseClassField()).isEqualTo(expected.getPrivateBaseClassField());
        }

        @Test
        void usingSuperclassSetters() {
            final SubClass expected = Instancio.create(SubClass.class);

            final SubClass result = Instancio.of(SubClass.class)
                    .set(setter(SubClass::setSubClassField), expected.getSubClassField())
                    .set(setter(BaseClass::setProtectedBaseClassField), expected.getProtectedBaseClassField())
                    .set(setter(BaseClass::setPrivateBaseClassField), expected.getPrivateBaseClassField())
                    .create();

            assertThat(result.getSubClassField()).isEqualTo(expected.getSubClassField());
            assertThat(result.getProtectedBaseClassField()).isEqualTo(expected.getProtectedBaseClassField());
            assertThat(result.getPrivateBaseClassField()).isEqualTo(expected.getPrivateBaseClassField());
        }
    }

    @Nested
    class AbstractTypeTest {

        @WithSettings
        private final Settings settings = SETTINGS;

        @Test
        @FeatureTag(Feature.UNSUPPORTED)
        void usingSetterFromInterfaceNotSupported() {
            final InstancioApi<ItemInterface<String>> api = Instancio.of(new TypeToken<ItemInterface<String>>() {})
                    .subtype(all(ItemInterface.class), Item.class)
                    .set(setter(ItemInterface<String>::setValue), "foo");

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContaining("setter(ItemInterface, \"setValue(Object)\")");
        }

        @Test
        void setterFromImplementationClass() {
            final SetMethodSelector<Item<String>, String> setValueMethod = Item::setValue;
            final ItemInterface<String> result = Instancio.of(new TypeToken<ItemInterface<String>>() {})
                    .subtype(all(ItemInterface.class), Item.class)
                    .set(setValueMethod, "foo")
                    .create();

            assertThat(result.getValue()).isEqualTo("foo");
        }
    }
}
