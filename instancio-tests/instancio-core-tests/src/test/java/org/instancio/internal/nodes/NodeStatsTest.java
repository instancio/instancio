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
package org.instancio.internal.nodes;

import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.internal.context.BooleanSelectorMap;
import org.instancio.test.support.pojo.misc.ClassWithoutFields;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.testsupport.fixtures.Nodes;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class NodeStatsTest {
    private static final NodeFactory NODE_FACTORY = Nodes.nodeFactory();

    @Test
    void person() {
        final InternalNode node = NODE_FACTORY.createRootNode(Person.class);
        final NodeStats stats = NodeStats.compute(node);

        final String expected = """
                <0:Person>
                 ├──<1:Person: String finalField>
                 ├──<1:Person: UUID uuid>
                 ├──<1:Person: String name>
                 ├──<1:Person: Address address>
                 │   ├──<2:Address: String address>
                 │   ├──<2:Address: String city>
                 │   ├──<2:Address: String country>
                 │   └──<2:Address: List<Phone> phoneNumbers>
                 │       └──<3:Phone>
                 │           ├──<4:Phone: String countryCode>
                 │           └──<4:Phone: String number>
                 ├──<1:Person: Gender gender>
                 ├──<1:Person: int age>
                 ├──<1:Person: LocalDateTime lastModified>
                 ├──<1:Person: Date date>
                 └──<1:Person: Pet[] pets>
                     └──<2:Pet>
                         └──<3:Pet: String name>
                """;

        assertThat(stats.getTreeString()).isEqualTo(expected);
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

        assertThat(stats.getTreeString()).isEqualTo(expected);
        assertThat(stats.getTotalNodes()).isOne();
        assertThat(stats.getHeight()).isZero();
    }

    @Test
    void withIgnoredNode() {
        final Set<TargetSelector> ignored = Collections.singleton(Select.field(StringsGhi::getH));
        final NodeContext ctx = Nodes.nodeContextBuilder()
                .ignoredSelectorMap(new BooleanSelectorMap(ignored))
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

        assertThat(stats.getTreeString()).isEqualTo(expected);
        assertThat(stats.getTotalNodes()).isEqualTo(8);
        assertThat(stats.getHeight()).isEqualTo(2);
    }
}
