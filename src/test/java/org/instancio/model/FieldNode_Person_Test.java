package org.instancio.model;


import org.instancio.testsupport.tags.ModelTag;

@ModelTag
class FieldNode_Person_Test {

//    private final NodeContext nodeContext = new NodeContext(Collections.emptyMap());
//
//    @Test
//    void address() {
//        final String rootField = "address";
//        final Node addressFieldNode = new Node(nodeContext, ReflectionUtils.getField(Person.class, rootField));
//
//        assertNode(addressFieldNode)
//                .hasFieldName(rootField)
//                .hasActualFieldType(Address.class)
//                .hasEmptyTypeMap()
//                .hasChildrenOfSize(4);
//
//        final FieldNode phoneNumbersFieldNode = addressFieldNode.getChildByFieldName("phoneNumbers");
//
//        assertFieldNode(phoneNumbersFieldNode)
//                .hasFieldName("phoneNumbers")
//                .hasActualFieldType(List.class)
//                .hasTypeMappedTo(getTypeVar(List.class, "E"), Phone.class)
//                .hasTypeMapWithSize(1)
//                .hasChildrenOfSize(1);
//
//        final ClassNode phoneClassNode = (ClassNode) phoneNumbersFieldNode.getChildren().get(0);
//
//        assertClassNode(phoneClassNode)
//                .hasParent(phoneNumbersFieldNode)
//                .hasKlass(Phone.class)
//                .hasChildrenOfSize(2);
//
//        assertFieldNode((FieldNode) NodeUtil.getChildNode(phoneClassNode, "countryCode"))
//                .hasParent(phoneClassNode)
//                .hasFieldName("countryCode")
//                .hasActualFieldType(String.class)
//                .hasEmptyTypeMap()
//                .hasNoChildren();
//
//        assertFieldNode((FieldNode) NodeUtil.getChildNode(phoneClassNode, "number"))
//                .hasParent(phoneClassNode)
//                .hasFieldName("number")
//                .hasActualFieldType(String.class)
//                .hasEmptyTypeMap()
//                .hasNoChildren();
//
//
//    }
}