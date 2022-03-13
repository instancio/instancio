package org.instancio.model;

import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.container.ItemContainer;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.testsupport.tags.ModelTag;
import org.instancio.testsupport.utils.CollectionUtils;
import org.instancio.testsupport.utils.NodeUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.testsupport.asserts.NodeAssert.assertNode;
import static org.instancio.testsupport.utils.TypeUtils.getTypeVar;

@ModelTag
class ClassNode_ItemContainer_Test {

    private static final Class<?> ROOT_CLASS = ItemContainer.class;
    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
        typeMap.put(getTypeVar(ROOT_CLASS, "X"), String.class);
        typeMap.put(getTypeVar(ROOT_CLASS, "Y"), LocalDateTime.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void classNode() {
        NodeFactory nodeFactory = new NodeFactory();

        final Node rootNode = nodeFactory.createNode(nodeContext, ROOT_CLASS, null, null, null);

        System.out.println(rootNode);

        assertNode(rootNode)
                .hasKlass(ROOT_CLASS)
                .hasEffectiveClass(ROOT_CLASS)
                .hasChildrenOfSize(7);


        assertItemsX(rootNode);
        assertItemsY(rootNode);

        assertPairValue(rootNode);

        final Node pairArrayNode = NodeUtils.getChildNode(rootNode, "pairArray");

        final Node pairListNode = NodeUtils.getChildNode(rootNode, "pairList");

    }

    private void assertPairValue(Node rootNode) {
        // Pair<X, Y> pairValue
        final Node pairValueNode = NodeUtils.getChildNode(rootNode, "pairValue");

        assertNode(pairValueNode)
                .hasParent(rootNode)
                .hasKlass(Pair.class)
                .hasEffectiveClass(Pair.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), getTypeVar(ItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), getTypeVar(ItemContainer.class, "Y"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "X"), String.class)
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "Y"), LocalDateTime.class)
                .hasTypeMapWithSize(4)
                .hasChildrenOfSize(2);

        assertNode(NodeUtils.getChildNode(pairValueNode, "left"))
                .hasParent(pairValueNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(String.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), getTypeVar(ItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), getTypeVar(ItemContainer.class, "Y"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "X"), String.class)
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "Y"), LocalDateTime.class)
                .hasTypeMapWithSize(4)
                .hasNoChildren();

        assertNode(NodeUtils.getChildNode(pairValueNode, "right"))
                .hasParent(pairValueNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(LocalDateTime.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), getTypeVar(ItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), getTypeVar(ItemContainer.class, "Y"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "X"), String.class)
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "Y"), LocalDateTime.class)
                .hasTypeMapWithSize(4)
                .hasNoChildren();
    }

    private void assertItemsX(Node rootNode) {
        final Node itemValueXNode = NodeUtils.getChildNode(rootNode, "itemValueX");
        assertNode(itemValueXNode)
                .hasParent(rootNode)
                .hasKlass(Item.class)
                .hasEffectiveClass(Item.class)
                .hasTypeMappedTo(getTypeVar(Item.class, "K"), getTypeVar(ItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "X"), String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(1);

        assertNode(CollectionUtils.getOnlyElement(itemValueXNode.getChildren()))
                .hasParent(itemValueXNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(String.class)
                .hasTypeMappedTo(getTypeVar(Item.class, "K"), getTypeVar(ItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "X"), String.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();

        final CollectionNode itemListXNode = (CollectionNode) NodeUtils.getChildNode(rootNode, "itemListX");

        assertNode(itemListXNode)
                .hasParent(rootNode)
                .hasKlass(List.class)
                .hasEffectiveClass(List.class)
//                .hasTypeMappedTo(getTypeVar(List.class, "E"), getTypeVar(GenericItem.class, "X"))
                //.hasTypeMappedTo(getTypeVar(GenericItemContainer.class, "X"), String.class)
                .hasTypeMapWithSize(1)
                .hasNoChildren();

        assertNode(itemListXNode.getElementNode()) // list element
                .hasParent(rootNode)
                .hasKlass(Item.class)
                .hasEffectiveClass(Item.class)
                .hasTypeMappedTo(getTypeVar(Item.class, "K"), getTypeVar(ItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "X"), String.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();
    }


    private void assertItemsY(Node rootNode) {
        final Node itemValueYNode = NodeUtils.getChildNode(rootNode, "itemValueY");
        assertNode(itemValueYNode)
                .hasParent(rootNode)
                .hasKlass(Item.class)
                .hasEffectiveClass(Item.class)
                .hasTypeMappedTo(getTypeVar(Item.class, "K"), getTypeVar(ItemContainer.class, "Y"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "Y"), LocalDateTime.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(1);

        assertNode(CollectionUtils.getOnlyElement(itemValueYNode.getChildren()))
                .hasParent(itemValueYNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(LocalDateTime.class)
                .hasTypeMappedTo(getTypeVar(Item.class, "K"), getTypeVar(ItemContainer.class, "Y"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "Y"), LocalDateTime.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();

        final CollectionNode itemListYNode = (CollectionNode) NodeUtils.getChildNode(rootNode, "itemListY");

        assertNode(itemListYNode)
                .hasParent(rootNode)
                .hasKlass(List.class)
                .hasEffectiveClass(List.class)
//                .hasTypeMappedTo(getTypeVar(List.class, "E"), getTypeVar(GenericItem.class, "X"))
                //.hasTypeMappedTo(getTypeVar(GenericItemContainer.class, "X"), LocalDateTime.class)
                .hasTypeMapWithSize(1)
                .hasNoChildren();

        assertNode(itemListYNode.getElementNode()) // list element
                .hasParent(rootNode)
                .hasKlass(Item.class)
                .hasEffectiveClass(Item.class)
                .hasTypeMappedTo(getTypeVar(Item.class, "K"), getTypeVar(ItemContainer.class, "Y"))
                .hasTypeMappedTo(getTypeVar(ItemContainer.class, "Y"), LocalDateTime.class)
                .hasTypeMapWithSize(2)
                .hasNoChildren();
    }
}
