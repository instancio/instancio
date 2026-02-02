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
package org.instancio.internal.nodes;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.testsupport.fixtures.Fixtures;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeclaredAndInheritedMemberCollectorTest {

    private final DeclaredAndInheritedMemberCollector collector = new DeclaredAndInheritedMemberCollector(
            Settings.defaults());

    private final InternalNode node = InternalNode.builder(
                    BaseClassSubClassInheritance.SubClass.class,
                    BaseClassSubClassInheritance.SubClass.class,
                    Fixtures.modelContext().getRootType())
            .build();

    @Test
    void shouldIncludeSubClassFields() {
        final ClassData result = collector.getClassData(node.toBuilder()
                .targetClass(BaseClassSubClassInheritance.SubClass.class)
                .build());

        assertThat(result.getMemberPairs())
                .extracting(MemberPair::getField)
                .extracting(Field::getName)
                .containsExactlyInAnyOrder(
                        "privateBaseClassField",
                        "protectedBaseClassField",
                        "subClassField");

        assertThat(result.getUnmatchedSetters()).isEmpty();
    }

    @ValueSource(classes = {
            Object.class, int.class, String.class, List.class, ArrayList.class, LocalDate.class, Person[].class})
    @ParameterizedTest
    void shouldReturnEmptyResults(final Class<?> klass) {
        final ClassData result = collector.getClassData(node.toBuilder()
                .targetClass(klass)
                .build());

        assertThat(result.getMemberPairs()).isEmpty();
        assertThat(result.getUnmatchedSetters()).isEmpty();
    }

    @Test
    void methodKeyEqualsAndHashCode() {
        EqualsVerifier.forClass(DeclaredAndInheritedMemberCollector.MethodKey.class)
                .suppress(Warning.NULL_FIELDS)
                .verify();
    }
}
