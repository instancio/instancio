package org.instancio.model;

import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Phone;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.instancio.testsupport.asserts.ClassNodeAssert.assertClassNode;
import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class FieldNode_Person_Test {

    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());

    @Test
    void address() {
        final String rootField = "address";
        final FieldNode addressFieldNode = new FieldNode(nodeContext, ReflectionUtils.getField(Person.class, rootField));

        assertFieldNode(addressFieldNode)
                .hasFieldName(rootField)
                .hasActualFieldType(Address.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(4);

        final FieldNode phoneNumbersFieldNode = addressFieldNode.getChildByFieldName("phoneNumbers");

        assertFieldNode(phoneNumbersFieldNode)
                .hasFieldName("phoneNumbers")
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Phone.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(1);

        final ClassNode phoneClassNode = (ClassNode) phoneNumbersFieldNode.getChildren().get(0);

        assertClassNode(phoneClassNode)
                .hasParent(phoneNumbersFieldNode)
                .hasKlass(Phone.class)
                .hasChildrenOfSize(2);

        assertFieldNode(NodeUtil.getChildFieldNode(phoneClassNode, "countryCode"))
                .hasParent(phoneClassNode)
                .hasFieldName("countryCode")
                .hasActualFieldType(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        assertFieldNode(NodeUtil.getChildFieldNode(phoneClassNode, "number"))
                .hasParent(phoneClassNode)
                .hasFieldName("number")
                .hasActualFieldType(String.class)
                .hasEmptyTypeMap()
                .hasNoChildren();


    }
}