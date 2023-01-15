package org.instancio.internal.nodes.resolvers;

import org.instancio.internal.nodes.NodeKind;
import org.instancio.test.support.java16.record.PersonRecord;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NodeKindRecordResolverTest {

    private final NodeKindRecordResolver resolver = new NodeKindRecordResolver();

    @Test
    void resolve() {
        assertThat(resolver.resolve(PersonRecord.class)).contains(NodeKind.RECORD);
        assertThat(resolver.resolve(Person.class)).isEmpty();
    }
}
