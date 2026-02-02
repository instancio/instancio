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
package org.instancio.test.features.selector;

import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Selector;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.Asserts.assertAllNulls;

@FeatureTag(Feature.METHOD_REFERENCE_SELECTOR)
@ExtendWith(InstancioExtension.class)
class GetMethodSelectorTest {

    @Test
    void simpleGetters() {
        final Person expected = Instancio.create(Person.class);

        final Person result = Instancio.of(Person.class)
                .set(field(Person::getUuid), expected.getUuid())
                .set(field(Person::getUuid), expected.getUuid())
                .set(field(Person::getName), expected.getName())
                .set(field(Person::getAge), expected.getAge())
                .set(field(Person::getGender), expected.getGender())
                .set(field(Person::getLastModified), expected.getLastModified())
                .set(field(Person::getPets), expected.getPets())
                .set(field(Address::getCity), expected.getAddress().getCity())
                .set(field(Address::getPhoneNumbers), expected.getAddress().getPhoneNumbers())
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
        final GetMethodSelector<Phone, String> getNumber = Phone::getNumber;
        final Phone result = Instancio.of(Phone.class)
                // passing method selector directly without wrapping as "field(getNumber)"
                .set(getNumber, "foo")
                .create();

        assertThat(result.getNumber()).isEqualTo("foo");
    }

    /**
     * For generic return types, the type must be specified explicitly.
     */
    @Nested
    class MethodWithGenericReturnTypeTest {
        @Test
        void genericItem() {
            final GetMethodSelector<Item<String>, String> getValueMethod = Item::getValue;

            final Item<String> result = Instancio.of(new TypeToken<Item<String>>() {})
                    .set(field(getValueMethod), "foo")
                    .create();

            assertThat(result.getValue()).isEqualTo("foo");
        }

        @Test
        void genericPair() {
            final GetMethodSelector<Pair<String, Integer>, String> getLeftMethod = Pair::getLeft;
            final GetMethodSelector<Pair<String, Integer>, Integer> getRightMethod = Pair::getRight;

            final Pair<String, Integer> result = Instancio.of(new TypeToken<Pair<String, Integer>>() {})
                    .set(getLeftMethod, "foo")
                    .set(getRightMethod, 123)
                    .create();

            assertThat(result.getLeft()).isEqualTo("foo");
            assertThat(result.getRight()).isEqualTo(123);
        }
    }

    @Test
    void propertyStyleGetter() {
        final PropertyStylePojo expected = Instancio.create(PropertyStylePojo.class);

        final GetMethodSelector<PropertyStylePojo, String> foo = PropertyStylePojo::foo;
        final GetMethodSelector<PropertyStylePojo, Integer> bar = PropertyStylePojo::bar;
        final GetMethodSelector<PropertyStylePojo, Boolean> isBaz = PropertyStylePojo::isBaz;
        final GetMethodSelector<PropertyStylePojo, Boolean> haz = PropertyStylePojo::haz;
        final GetMethodSelector<PropertyStylePojo, Boolean> hasGaz = PropertyStylePojo::hasGaz;

        final PropertyStylePojo result = Instancio.of(PropertyStylePojo.class)
                .set(foo, expected.foo())
                .set(bar, expected.bar())
                .set(isBaz, expected.isBaz())
                .set(haz, expected.haz())
                .set(hasGaz, expected.hasGaz())
                .create();

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void group() {
        final Person result = Instancio.of(Person.class)
                .ignore(all(
                        field(Person::getUuid),
                        field(Person::getGender),
                        field(Person::getLastModified)))
                .create();

        assertAllNulls(result.getUuid(), result.getGender(), result.getLastModified());
    }

    @Test
    void withinScope() {
        final RichPerson result = Instancio.of(RichPerson.class)
                .set(field(Address::getCity).within(field(RichPerson::getAddress1).toScope()), "foo")
                .set(field(Address::getCity).within(field(RichPerson::getAddress2).toScope()), "bar")
                .create();

        assertThat(result.getAddress1().getCity()).isEqualTo("foo");
        assertThat(result.getAddress2().getCity()).isEqualTo("bar");
    }

    @Nested
    class InheritanceTest {
        @Test
        void usingSubclassGetters() {
            final SubClass expected = Instancio.create(SubClass.class);

            final SubClass result = Instancio.of(SubClass.class)
                    .set(field(SubClass::getSubClassField), expected.getSubClassField())
                    .set(field(SubClass::getProtectedBaseClassField), expected.getProtectedBaseClassField())
                    .set(field(SubClass::getPrivateBaseClassField), expected.getPrivateBaseClassField())
                    .create();

            assertThat(result.getSubClassField()).isEqualTo(expected.getSubClassField());
            assertThat(result.getProtectedBaseClassField()).isEqualTo(expected.getProtectedBaseClassField());
            assertThat(result.getPrivateBaseClassField()).isEqualTo(expected.getPrivateBaseClassField());
        }

        @Test
        void usingSuperclassGetters() {
            final SubClass expected = Instancio.create(SubClass.class);

            final SubClass result = Instancio.of(SubClass.class)
                    .set(field(SubClass::getSubClassField), expected.getSubClassField())
                    .set(field(BaseClass::getProtectedBaseClassField), expected.getProtectedBaseClassField())
                    .set(field(BaseClass::getPrivateBaseClassField), expected.getPrivateBaseClassField())
                    .create();

            assertThat(result.getSubClassField()).isEqualTo(expected.getSubClassField());
            assertThat(result.getProtectedBaseClassField()).isEqualTo(expected.getProtectedBaseClassField());
            assertThat(result.getPrivateBaseClassField()).isEqualTo(expected.getPrivateBaseClassField());
        }
    }

    @Nested
    class AbstractTypeTest {

        @Test
        @FeatureTag(Feature.UNSUPPORTED)
        void usingGetterFromInterfaceNotSupported() {
            final InstancioApi<ItemInterface<String>> api = Instancio.of(new TypeToken<ItemInterface<String>>() {});
            final GetMethodSelector<ItemInterface<String>, String> getValueMethod = ItemInterface::getValue;
            final Selector selector = field(getValueMethod);

            assertThatThrownBy(() -> api.set(selector, "foo"))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining(String.format(
                            "Unable to resolve the field from method reference:%n" +
                                    "-> ItemInterface::getValue"));
        }

        @Test
        void getterFromImplementationClass() {
            final GetMethodSelector<Item<String>, String> getValueMethod = Item::getValue;
            final ItemInterface<String> result = Instancio.of(new TypeToken<ItemInterface<String>>() {})
                    .subtype(all(ItemInterface.class), Item.class)
                    .set(field(getValueMethod), "foo")
                    .create();

            assertThat(result.getValue()).isEqualTo("foo");
        }
    }

    @Nested
    class OverloadedGetterTest {
        static class Pojo {
            String foo;

            String getFoo() {
                return foo;
            }

            @SuppressWarnings("unused")
            String getFoo(String defaultFoo) {
                return foo == null ? defaultFoo : foo;
            }
        }

        @Test
        void overloaded() {
            final GetMethodSelector<Pojo, String> getFooMethod = Pojo::getFoo;

            final Pojo result = Instancio.of(Pojo.class)
                    .set(field(getFooMethod), "foo")
                    .create();

            assertThat(result.getFoo()).isEqualTo("foo");
        }
    }
}
