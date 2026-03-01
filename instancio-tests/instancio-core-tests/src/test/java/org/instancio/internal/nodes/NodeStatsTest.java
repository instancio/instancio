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

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.internal.selectors.TargetField;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.pojo.dynamic.DynPhone;
import org.instancio.test.support.pojo.misc.ClassWithoutFields;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.testsupport.fixtures.Fixtures;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NodeStatsTest {
    private static final NodeFactory NODE_FACTORY = Fixtures.nodeFactory();

    /**
     * {@link Class#getDeclaredFields()} ()} does not guarantee the order of returned fields.
     * But it seems the fields are always returned in the order they are declared.
     */
    @Test
    void person() {
        final InternalNode node = NODE_FACTORY.createRootNode(Person.class);
        final NodeStats stats = NodeStats.compute(node);

        final String expected = """
                <0:Person>
                 ├──<1:Person: String finalField>
                 ├──<1:Person: UUID uuid; setUuid(UUID)>
                 ├──<1:Person: String name; setName(String)>
                 ├──<1:Person: Address address; setAddress(Address)>
                 │   ├──<2:Address: String street; setStreet(String)>
                 │   ├──<2:Address: String city; setCity(String)>
                 │   ├──<2:Address: String country; setCountry(String)>
                 │   └──<2:Address: List<Phone> phoneNumbers; setPhoneNumbers(List<Phone>)>
                 │       └──<3:Phone>
                 │           ├──<4:Phone: String countryCode; setCountryCode(String)>
                 │           └──<4:Phone: String number; setNumber(String)>
                 ├──<1:Person: Gender gender; setGender(Gender)>
                 ├──<1:Person: int age; setAge(int)>
                 ├──<1:Person: LocalDateTime lastModified; setLastModified(LocalDateTime)>
                 ├──<1:Person: Date date; setDate(Date)>
                 └──<1:Person: Pet[] pets; setPets(Pet[])>
                     └──<2:Pet>
                         └──<3:Pet: String name; setName(String)>
                """;

        assertThat(stats.getTreeString()).isEqualToNormalizingNewlines(expected);
        assertThat(stats.getTotalNodes()).isEqualTo(19);
        assertThat(stats.getHeight()).isEqualTo(4);
    }

    @Test
    void emptyNode() {
        final InternalNode node = NODE_FACTORY.createRootNode(ClassWithoutFields.class);
        final NodeStats stats = NodeStats.compute(node);

        final String expected = """
                <0:ClassWithoutFields>
                """;

        assertThat(stats.getTreeString()).isEqualToNormalizingNewlines(expected);
        assertThat(stats.getTotalNodes()).isOne();
        assertThat(stats.getHeight()).isZero();
    }

    @Test
    void withIgnoredNode() {
        final SelectorImpl fieldH = SelectorImpl.builder(new TargetField(ReflectionUtils.getField(StringsGhi.class, "h")))
                .build();

        final ModelContext ctx = ModelContext.builder(StringsDef.class)
                .withSettings(Settings.defaults())
                .withIgnored(fieldH)
                .build();

        final NodeFactory nodeFactory = new NodeFactory(ctx);
        final InternalNode node = nodeFactory.createRootNode(StringsDef.class);

        final NodeStats stats = NodeStats.compute(node);

        final String expected = """
                <0:StringsDef>
                 ├──<1:StringsDef: String d>
                 ├──<1:StringsDef: String e>
                 ├──<1:StringsDef: String f>
                 └──<1:StringsDef: StringsGhi ghi>
                     ├──<2:StringsGhi: String g>
                     ├──<2:StringsGhi: String h [IGNORED]>
                     └──<2:StringsGhi: String i>
                """;

        assertThat(stats.getTreeString()).isEqualToNormalizingNewlines(expected);
        assertThat(stats.getTotalNodes()).isEqualTo(8);
        assertThat(stats.getHeight()).isEqualTo(2);
    }

    @Test
    void withCyclicNode() {
        final InternalNode node = NODE_FACTORY.createRootNode(MainPojo.class);
        final NodeStats stats = NodeStats.compute(node);

        final String expected = """
                <0:MainPojo>
                 ├──<1:MainPojo: Long id; setId(Long)>
                 └──<1:MainPojo: List<DetailPojo> detailPojos; setDetailPojos(List<DetailPojo>)>
                     └──<2:DetailPojo>
                         ├──<3:DetailPojo: Long id; setId(Long)>
                         ├──<3:DetailPojo: Long mainPojoId; setMainPojoId(Long)>
                         └──<3:DetailPojo: MainPojo mainPojo; setMainPojo(MainPojo) [CYCLIC]>
                """;

        assertThat(stats.getTreeString()).isEqualToNormalizingNewlines(expected);
    }

    @Test
    void dynPhone() {
        final InternalNode node = NODE_FACTORY.createRootNode(DynPhone.class);
        final NodeStats stats = NodeStats.compute(node);

        final String expected = """
                <0:DynPhone>
                 ├──<1:DynPhone: Map<String, Object> data>
                 │   ├──<2:String>
                 │   └──<2:Object>
                 ├──<1:DynPhone: setCountryCode(String)>
                 └──<1:DynPhone: setNumber(String)>
                """;

        assertThat(stats.getTreeString()).isEqualToNormalizingNewlines(expected);
        assertThat(stats.getTotalNodes()).isEqualTo(6);
        assertThat(stats.getHeight()).isEqualTo(2);
    }

}
