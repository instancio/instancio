package org.instancio.model;

import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Phone;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

class FieldNode_Person_Test {

    @Test
    void address() {
        final String rootField = "address";
        final FieldNode node = new FieldNode(ReflectionUtils.getField(Person.class, rootField),
                Collections.emptyMap()); // empty type map

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(Address.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(4);

        final FieldNode phoneNumbersFieldNode = node.getChildByFieldName("phoneNumbers");

        assertFieldNode(phoneNumbersFieldNode)
                .hasFieldName("phoneNumbers")
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Phone.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(0);
    }
}