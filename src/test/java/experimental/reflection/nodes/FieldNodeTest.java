package experimental.reflection.nodes;

import org.instancio.pojo.circular.IndirectCircularRef;
import org.instancio.pojo.generics.MiscFields;
import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.GenericItemContainer;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.pojo.generics.container.Triplet;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.generics.outermidinner.Outer;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Phone;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.testsupport.asserts.FieldNodeAssert.assertFieldNode;

class FieldNodeTest {

    private final Map<Type, Class<?>> classMiscFieldsTypeMap = new HashMap<>();
    private final Map<Type, Class<?>> classGenericItemContainerTypeMap = new HashMap<>();

    private static TypeVariable<?> getTypeVar(Class<?> klass, String typeParameter) {
        for (TypeVariable<?> tvar : klass.getTypeParameters()) {
            if (tvar.getName().equals(typeParameter)) {
                return tvar;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid type parameter '%s' for %s", typeParameter, klass));
    }

    @BeforeEach
    void setUp() {
        classMiscFieldsTypeMap.put(getTypeVar(MiscFields.class, "A"), Long.class);
        classMiscFieldsTypeMap.put(getTypeVar(MiscFields.class, "B"), String.class);
        classMiscFieldsTypeMap.put(getTypeVar(MiscFields.class, "C"), Integer.class);

        classGenericItemContainerTypeMap.put(getTypeVar(GenericItemContainer.class, "X"), String.class);
        classGenericItemContainerTypeMap.put(getTypeVar(GenericItemContainer.class, "Y"), LocalDateTime.class);
    }

    @Test
    void test_MiscFields_pairAPairIntegerString() {

        // class Pair<L, R> {}
        //
        // class MiscFields<A, B, C> {
        //     Pair<A, Pair<Integer, String>> pairAPairIntegerString
        // }
        //
        // Pair.L -> MiscFields.A;
        // MiscFields.A -> Long.class
        // Pair.R -> Pair.class
        //
        // classMiscFieldsTypeMap.put("A", Long.class);
        // classMiscFieldsTypeMap.put("B", String.class);
        // classMiscFieldsTypeMap.put("C", Integer.class);

        final FieldNode node = new FieldNode(ReflectionUtils.getField(MiscFields.class, "pairAPairIntegerString"),
                classMiscFieldsTypeMap);

        System.out.println(node);
        assertFieldNode(node)
                .hasActualFieldType(Pair.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), getTypeVar(MiscFields.class, "A"))
                .hasTypeMappedTo(getTypeVar(MiscFields.class, "A"), Long.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), Pair.class)
                .hasTypeMapWithSize(3)
                .hasChildrenOfSize(2);

        assertFieldNode(node.getChildByTypeParameter("L"))
                .hasActualFieldType(Long.class)
                .hasEmptyTypeMap()
                .hasNoChildren();

        assertFieldNode(node.getChildByTypeParameter("R"))
                .hasActualFieldType(Pair.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "L"), Integer.class)
                .hasTypeMappedTo(getTypeVar(Pair.class, "R"), String.class)
                .hasChildrenOfSize(2);
    }

    @Test
    void test_MiscFields_tripletA_FooBarBazString_ListOfC() {
        final String rootField = "tripletA_FooBarBazString_ListOfC";
        final FieldNode node = new FieldNode(ReflectionUtils.getField(MiscFields.class, rootField),
                classMiscFieldsTypeMap);

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(Triplet.class)
                .hasTypeMappedTo(getTypeVar(Triplet.class, "M"), getTypeVar(MiscFields.class, "A"))
                .hasTypeMappedTo(getTypeVar(Triplet.class, "N"), Foo.class)
                .hasTypeMappedTo(getTypeVar(Triplet.class, "O"), List.class)
                .hasTypeMappedTo(getTypeVar(MiscFields.class, "A"), Long.class)
                .hasTypeMapWithSize(4)
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
                .hasTypeMappedTo(getTypeVar(Foo.class, "X"), Bar.class)
                .hasTypeMapWithSize(1);

        final FieldNode rightField = node.getChildByTypeParameter("O");
        assertFieldNode(rightField)
                .hasFieldName("right")
                .hasActualFieldType(List.class)
                .hasNoChildren()
                .hasTypeMappedTo(getTypeVar(List.class, "E"), getTypeVar(MiscFields.class, "C"))
                .hasTypeMappedTo(getTypeVar(MiscFields.class, "C"), Integer.class)
                .hasTypeMapWithSize(2);

        final FieldNode fooValueFieldNode = midField.getChildByFieldName("fooValue");
        assertFieldNode(fooValueFieldNode)
                .hasFieldName("fooValue")
                .hasActualFieldType(Bar.class)
                .hasChildrenOfSize(2)
                .hasTypeMappedTo(getTypeVar(Bar.class, "Y"), Baz.class)
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
                .hasTypeMappedTo(getTypeVar(Baz.class, "Z"), String.class)
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
        final FieldNode bFieldNode = new FieldNode(ReflectionUtils.getField(IndirectCircularRef.A.class, rootField),
                Collections.emptyMap()); // empty type map

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

    @Test
    void test_ListOfOuterMidInnerString_listOfOuter() {
        final String rootField = "listOfOuter";
        final FieldNode node = new FieldNode(ReflectionUtils.getField(ListOfOuterMidInnerString.class, rootField),
                Collections.emptyMap());

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(List.class)
                .hasTypeMappedTo(getTypeVar(List.class, "E"), Outer.class)
                .hasTypeMapWithSize(1)
                .hasChildrenOfSize(0); // TODO

        System.out.println(node);
    }

    @Test
    void test_Person_address() {
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

    @Test
    void test_GenericItemContainer_itemValueL() {
        final String rootField = "itemValueL";
        final FieldNode node = new FieldNode(ReflectionUtils.getField(GenericItemContainer.class, rootField),
                classGenericItemContainerTypeMap);

        assertFieldNode(node)
                .hasFieldName(rootField)
                .hasActualFieldType(GenericItem.class)
                .hasTypeMappedTo(getTypeVar(GenericItem.class, "K"), getTypeVar(GenericItemContainer.class, "X"))
                .hasTypeMappedTo(getTypeVar(GenericItemContainer.class, "X"), String.class)
                .hasTypeMapWithSize(2)
                .hasChildrenOfSize(1);

    }
}