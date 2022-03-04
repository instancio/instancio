package experimental.reflection.nodes;

import org.instancio.pojo.circular.IndirectCircularRef;
import org.instancio.pojo.generics.MiscFields;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.pojo.generics.container.Triplet;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;

class FieldNodeTest {

    private final Map<String, Class<?>> rootTypeMap = new HashMap<>();

    @BeforeEach
    void setUp() {
        rootTypeMap.put("A", Long.class);
        rootTypeMap.put("B", String.class);
        rootTypeMap.put("C", Integer.class);
    }

    @Test
    void test_MiscFields_pairAPairIntegerString() {
        final FieldNode node = new FieldNode(ReflectionUtils.getField(MiscFields.class, "pairAPairIntegerString"), rootTypeMap);

        assertFieldNode(node)
                .hasActualFieldType(Pair.class)
                .hasTypeMappedTo("L", "A")
                .hasTypeMappedTo("R", Pair.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(2);

        assertFieldNode(node.getChildByTypeParameter("L"))
                .hasActualFieldType(Long.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        assertFieldNode(node.getChildByTypeParameter("R"))
                .hasActualFieldType(Pair.class)
                .hasTypeMappedTo("L", Integer.class)
                .hasTypeMappedTo("R", String.class)
                .hasChildrenOfSize(2);
    }

    @Test
    void test_MiscFields_tripletA_FooBarBazString_ListOfC() {
        final String rootField = "tripletA_FooBarBazString_ListOfC";
        final FieldNode node = new FieldNode(ReflectionUtils.getField(MiscFields.class, rootField), rootTypeMap);

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(Triplet.class)
                .hasTypeMappedTo("M", "A")
                .hasTypeMappedTo("N", Foo.class)
                .hasTypeMappedTo("O", List.class)
                .hasTypeMapWithSize(3)
                .hasChildrenOfSize(3);

        final FieldNode leftField = node.getChildByTypeParameter("M");
        assertFieldNode(leftField)
                .hasFieldName("left")
                .hasActualFieldType(Long.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        final FieldNode midField = node.getChildByTypeParameter("N");
        assertFieldNode(midField)
                .hasFieldName("mid")
                .hasActualFieldType(Foo.class)
                .hasChildrenOfSize(2)
                .hasTypeMappedTo("X", Bar.class)
                .hasTypeMapWithSize(1);

        final FieldNode rightField = node.getChildByTypeParameter("O");
        assertFieldNode(rightField)
                .hasFieldName("right")
                .hasActualFieldType(List.class)
                .hasNoChildren()
                .hasTypeMappedTo("E", "C")
                .hasTypeMapWithSize(1);

        final FieldNode fooValueFieldNode = midField.getChildByFieldName("fooValue");
        assertFieldNode(fooValueFieldNode)
                .hasFieldName("fooValue")
                .hasActualFieldType(Bar.class)
                .hasChildrenOfSize(2)
                .hasTypeMappedTo("Y", Baz.class)
                .hasTypeMapWithSize(1);

        final FieldNode otherFooValueFieldNode = midField.getChildByFieldName("otherFooValue");
        assertFieldNode(otherFooValueFieldNode)
                .hasFieldName("otherFooValue")
                .hasActualFieldType(Object.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        final FieldNode barValueFieldNode = fooValueFieldNode.getChildByFieldName("barValue");
        assertFieldNode(barValueFieldNode)
                .hasFieldName("barValue")
                .hasActualFieldType(Baz.class)
                .hasChildrenOfSize(1)
                .hasTypeMappedTo("Z", String.class)
                .hasTypeMapWithSize(1);

        final FieldNode bazValueFieldNode = barValueFieldNode.getChildByFieldName("bazValue");
        assertFieldNode(bazValueFieldNode)
                .hasFieldName("bazValue")
                .hasActualFieldType(String.class)
                .hasNoChildren()
                .hasEmptyTypeMap();
    }


    @Test
    void test_IndirectCircularRef_b() {
        final String rootField = "b";
        final FieldNode bFieldNode = new FieldNode(ReflectionUtils.getField(IndirectCircularRef.A.class, rootField), rootTypeMap);

        assertFieldNode(bFieldNode)
                .hasFieldName(rootField)
                .hasActualFieldType(IndirectCircularRef.B.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        final FieldNode cFieldNode = bFieldNode.getChildByFieldName("c");
        assertFieldNode(cFieldNode)
                .hasFieldName("c")
                .hasActualFieldType(IndirectCircularRef.C.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        final FieldNode aFieldNode = cFieldNode.getChildByFieldName("a");
        assertFieldNode(aFieldNode)
                .hasFieldName("a")
                .hasActualFieldType(IndirectCircularRef.A.class)
                .hasEmptyTypeMap()
                .hasChildrenOfSize(1);

        final FieldNode cyclicBFieldNode = aFieldNode.getChildByFieldName(rootField);
        assertFieldNode(cyclicBFieldNode)
                .hasFieldName(rootField) // back to root field
                .hasActualFieldType(IndirectCircularRef.B.class)
                .hasEmptyTypeMap()
                .hasNoChildren();  // but no children this time
    }
}