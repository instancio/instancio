package org.instancio.model;

import org.instancio.pojo.generics.container.Pair;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.FooBarBazContainer;
import org.instancio.testsupport.tags.ModelTag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.CollectionUtils;

import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import static org.instancio.testsupport.asserts.ClassNodeAssert.assertClassNode;

@ModelTag
class ClassNode_FooBarBazContainer_Test {

    private NodeContext nodeContext;

    @BeforeEach
    void setUp() {
        Map<TypeVariable<?>, Class<?>> typeMap = new HashMap<>();
//        typeMap.put(getTypeVar(Pair.class, "L"), String.class);
//        typeMap.put(getTypeVar(Pair.class, "R"), Integer.class);

        nodeContext = new NodeContext(typeMap);
    }

    @Test
    void classNode() {

        ClassNode rootNode = new ClassNode(nodeContext, null, FooBarBazContainer.class, null, null);

        System.out.println(rootNode);

        assertClassNode(rootNode)
                .hasKlass(FooBarBazContainer.class)
                .hasParent(null)
                .hasChildrenOfSize(1);

        final ClassNode itemNode = (ClassNode) CollectionUtils.getOnlyElement(rootNode.getChildren());
        assertClassNode(itemNode)
                .hasKlass(Foo.class)
                .hasEffectiveClass(Foo.class)
                .hasGenericTypeName("org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Foo<org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Bar<org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Baz<java.lang." +
                        "String>>>")
                .hasChildrenOfSize(2);

        final ClassNode fooValueNode = (ClassNode) NodeUtil.getChildNode(itemNode, "fooValue");
        assertClassNode(fooValueNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(Bar.class)
                .hasGenericTypeName("org.instancio.pojo.generics.foobarbaz.itemcontainer." +  // FIXME incorrect
                        "Foo<org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Bar<org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Baz<java.lang." +
                        "String>>>")
                .hasChildrenOfSize(2);

        //final ClassNode barValueNode = NodeUtil.getChildClassNode(fooValueNode, Bar.class);
        final ClassNode barValueNode = (ClassNode) fooValueNode.getChildren().get(0);

        assertClassNode(barValueNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(Baz.class)
                .hasGenericTypeName("org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Bar<org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Baz<java.lang.String>>")
                .hasChildrenOfSize(1);


        final ClassNode otherFooValueNode = (ClassNode) NodeUtil.getChildNode(itemNode, "otherFooValue");
        assertClassNode(otherFooValueNode)
                .hasKlass(Object.class)
                .hasEffectiveClass(Object.class)
                .hasGenericTypeName("org.instancio.pojo.generics.foobarbaz.itemcontainer." +  // FIXME incorrect.. should be object
                        "Foo<org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Bar<org.instancio.pojo.generics.foobarbaz.itemcontainer." +
                        "Baz<java.lang." +
                        "String>>>")
                .hasNoChildren();

    }
}
