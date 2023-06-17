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
package org.instancio.internal.context;

import org.instancio.GroupableSelector;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeFactory;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.PersonHolder;
import org.instancio.test.support.pojo.person.Pet;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.RichPerson;
import org.instancio.testsupport.fixtures.Nodes;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;

class SelectorMapTest {
    private final NodeFactory nodeFactory = Nodes.nodeFactory();

    private final InternalNode rootNode = nodeFactory.createRootNode(PersonHolder.class);
    private final InternalNode personNameNode = getNodeWithField(rootNode, Person.class, "name");
    private final InternalNode phoneNumberNode = getNodeWithField(rootNode, Phone.class, "number");
    private final InternalNode petNameNode = getNodeWithField(rootNode, Pet.class, "name");
    // RichPerson.phone.number
    private final InternalNode richPersonPhoneFieldNumberFieldNode = getNodeWithField(
            getNodeWithField(rootNode, RichPerson.class, "phone"), Phone.class, "number");
    // RichPerson.address1.List<Phone>.number
    private final InternalNode richPersonListOfPhonesPhoneNumberFieldNode = getNodeWithField(
            getNodeWithField(rootNode, RichPerson.class, "address1"), Phone.class, "number");

    private final SelectorMap<String> selectorMap = new SelectorMap<>();

    private void put(final TargetSelector selector, final String value) {
        selectorMap.put(cast(selector), value);
    }

    @Test
    void getValues() {
        put(allStrings().within(scope(RichPerson.class, "phone")), "foo");
        // Given two equal selector keys, last value overwrites the first one
        put(allStrings().within(scope(RichPerson.class, "address1"), scope(Phone.class)), "bar");
        put(allStrings().within(scope(RichPerson.class, "address1"), scope(Phone.class)), "baz");

        assertThat(selectorMap.getValue(richPersonPhoneFieldNumberFieldNode)).contains("foo");
        assertThat(selectorMap.getValue(richPersonListOfPhonesPhoneNumberFieldNode)).contains("baz");

        // get all values
        assertThat(selectorMap.getValues(richPersonPhoneFieldNumberFieldNode)).containsOnly("foo");
        assertThat(selectorMap.getValues(richPersonListOfPhonesPhoneNumberFieldNode)).containsOnly("baz");
    }

    @Test
    void precedence() {
        put(field(Person.class, "name"), "foo");
        put(field(Person.class, "name"), "bar");
        assertThat(selectorMap.getValue(personNameNode)).contains("bar");
    }

    @Test
    void precedenceWithScope() {
        put(field(Person.class, "name").within(scope(String.class)), "foo");
        put(field(Person.class, "name").within(scope(String.class)), "bar");
        assertThat(selectorMap.getValue(personNameNode)).contains("bar");
    }

    @MethodSource("phoneNumberFieldMatchingSelectors")
    @ParameterizedTest
    void phoneNumberFieldScopeMatches(TargetSelector selector) {
        put(selector, "foo");
        assertThat(selectorMap.getValue(phoneNumberNode)).contains("foo");
    }

    private static Stream<Arguments> phoneNumberFieldMatchingSelectors() {
        return Stream.of(
                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(String.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Person.class, "address"))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Address.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(List.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Person.class, "address"),
                        scope(Address.class, "phoneNumbers"),
                        scope(Phone.class, "number"))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Person.class),
                        scope(Address.class),
                        scope(List.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Phone.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Address.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(List.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Phone.class, "number")))
        );
    }

    @MethodSource("phoneNumberFieldNonMatchingSelectors")
    @ParameterizedTest
    void phoneNumberFieldScopeNonMatches(TargetSelector selector) {
        put(selector, "foo");
        assertThat(selectorMap.getValue(phoneNumberNode)).isEmpty();
    }

    private static Stream<Arguments> phoneNumberFieldNonMatchingSelectors() {
        return Stream.of(
                Arguments.of(field(Phone.class, "number").within(
                        scope(Pet.class))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Phone.class, "countryCode"))),

                Arguments.of(field(Phone.class, "number").within(
                        scope(Object.class)))
        );
    }

    @Test
    void allStringsMatching() {
        put(allStrings().within(scope(Pet.class)), "foo");
        assertThat(selectorMap.getValue(petNameNode)).contains("foo");
        assertThat(selectorMap.getValue(personNameNode)).isEmpty();
        assertThat(selectorMap.getValue(phoneNumberNode)).isEmpty();

        final InternalNode stringNode = nodeFactory.createRootNode(String.class);
        assertThat(selectorMap.getValue(stringNode)).isEmpty();
    }

    private static InternalNode getNodeWithField(final InternalNode node, final Class<?> declaringClass, final String fieldName) {
        final Field field = ReflectionUtils.getField(declaringClass, fieldName);
        assertThat(field).as("null field").isNotNull();
        if (Objects.equals(node.getField(), field)) {
            return node;
        }

        InternalNode result = null;
        for (InternalNode child : node.getChildren()) {
            result = getNodeWithField(child, declaringClass, fieldName);
            if (result != null) break;
        }
        return result;
    }

    @Nested
    class ToStringTest {
        @Test
        void verifyEmptyMapToString() {
            assertThat(selectorMap).hasToString("SelectorMap{}");
        }

        @Test
        void verifyToString() {
            put(Select.all(byte.class), "foo");
            put(Select.field(Phone.class, "number"), "bar");

            assertThat(selectorMap).hasToString(String.format("SelectorMap:{%n" +
                    "all(byte)=foo%n" +
                    "field(Phone, \"number\")=bar%n" +
                    "}"));
        }
    }

    @Nested
    class UnusedKeysTest {
        @Test
        void emptyMap() {
            assertThat(selectorMap.getUnusedKeys()).isEmpty();
        }

        @Test
        void unusedUsingGetValue() {
            final Selector nameSelector = field(Person.class, "name");
            final GroupableSelector addressOnePhoneSelector = allStrings().within(
                    scope(RichPerson.class, "address1"),
                    scope(Phone.class));

            assertThat(selectorMap.getUnusedKeys()).isEmpty();

            put(nameSelector, "foo");
            put(addressOnePhoneSelector, "bar");

            assertThat(selectorMap.getUnusedKeys()).hasSize(2);

            // non-matching key
            selectorMap.getValue(petNameNode);
            assertThat(selectorMap.getUnusedKeys()).hasSize(2);

            // get name
            selectorMap.getValue(personNameNode);
            assertThat(selectorMap.getUnusedKeys()).containsExactlyInAnyOrder(addressOnePhoneSelector);

            // get address1...phone
            selectorMap.getValue(richPersonListOfPhonesPhoneNumberFieldNode);
            assertThat(selectorMap.getUnusedKeys()).isEmpty();
        }

        @Test
        void unusedUsingGetValues() {
            final Selector nameSelector1 = field(Person.class, "name");
            final GroupableSelector nameSelector2 = field(Person.class, "name")
                    .within(scope(Person.class));

            put(nameSelector1, "foo");
            put(nameSelector2, "bar");

            assertThat(selectorMap.getUnusedKeys()).hasSize(2);

            // matches all selectors
            selectorMap.getValues(personNameNode);
            assertThat(selectorMap.getUnusedKeys()).isEmpty();
        }
    }

    private static SelectorImpl cast(final TargetSelector selector) {
        return (SelectorImpl) selector;
    }
}
