package org.instancio.util;

import experimental.reflection.nodes.GenericType;
import lombok.Getter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.instancio.Pair;
import org.instancio.pojo.generics.FooContainer;
import org.instancio.pojo.generics.MiscFields;
import org.instancio.pojo.generics.container.Triplet;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.FooBarBazContainer;
import org.instancio.pojo.generics.outermidinner.Inner;
import org.instancio.pojo.generics.outermidinner.ListOfOuterMidInnerString;
import org.instancio.pojo.generics.outermidinner.Mid;
import org.instancio.pojo.generics.outermidinner.Outer;
import org.instancio.pojo.inheritance.Inheritance;
import org.instancio.pojo.person.Address;
import org.instancio.pojo.person.Person;
import org.instancio.pojo.person.Phone;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilsTest {

    @Getter
    @ToString
    static class SomeClass<T> {

        private final Supplier<? extends T> ctor;

        private T field;

        SomeClass(Supplier<? extends T> ctor) {
            this.ctor = Objects.requireNonNull(ctor);
        }

        public void myMethod() {
            field = ctor.get();
        }
    }

    @Test
    void getDeclaredAndSuperclassFields() {
        List<Field> results = ReflectionUtils.getDeclaredAndSuperclassFields(Inheritance.ChildClass.class);

        assertThat(results.stream().map(Field::getName))
                .containsExactly("childField", "privateBaseField", "protectedBaseField");
    }

    @Test
    void someClass() {
        SomeClass<StringBuilder> it = new SomeClass<>(StringBuilder::new);
        it.myMethod();
        System.out.println(it);
    }

    @Test
    void fooBarBazContainerManual() throws Exception {
        final Class<?> rootClass = FooBarBazContainer.class;
        final Field itemField = ReflectionUtils.getField(rootClass, "item");
        final FooBarBazContainer container = ReflectionUtils.instantiate(FooBarBazContainer.class);

        System.out.println("item genericType: " + itemField.getGenericType());
        System.out.println("item genericType: " + ((ParameterizedType) itemField.getGenericType()).getActualTypeArguments()[0]);
        System.out.println("item genericType: " + Arrays.toString(itemField.getType().getTypeParameters()));

        final Type itemType = itemField.getGenericType();
        System.out.println("itemType: " + itemType);

        final Field fooValueField = ReflectionUtils.getField(Foo.class, "fooValue");
        System.out.println("item genericType: " + fooValueField.getGenericType());
        // fails: System.out.println("item genericType: " + ((ParameterizedType) fooValueField.getGenericType()).getActualTypeArguments()[0]);
        System.out.println("item genericType: " + Arrays.toString(fooValueField.getType().getTypeParameters()));


        final ParameterizedType fooType = (ParameterizedType) itemType;

        final Class<?> fooClass = (Class<?>) fooType.getRawType();
        final ParameterizedType barType = (ParameterizedType) fooType.getActualTypeArguments()[0];

        final Class<?> barClass = (Class<?>) barType.getRawType();
        final ParameterizedType bazType = (ParameterizedType) barType.getActualTypeArguments()[0];

        final Class<?> bazClass = (Class<?>) bazType.getRawType();
        final Class<?> stringClass = (Class<?>) bazType.getActualTypeArguments()[0];


        System.out.println("type1: " + fooType + ", " + barType);
        System.out.println("fooClass: " + fooClass + ", typeParam " + fooClass.getTypeParameters()[0]);
        System.out.println("barClass: " + barClass + ", typeParam " + barClass.getTypeParameters()[0]);
        System.out.println("bazClass: " + bazClass + ", typeParam " + bazClass.getTypeParameters()[0]);
        System.out.println("stringClass: " + stringClass);


    }

    @Test
    void fooContainer() {

        final FooContainer container = ReflectionUtils.instantiate(FooContainer.class);

        final Field itemField = ReflectionUtils.getField(FooContainer.class, "item");
        System.out.println("item genericType: " + itemField.getGenericType());
        System.out.println("item genericType: " + ((ParameterizedType) itemField.getGenericType()).getActualTypeArguments()[0]);
        System.out.println("item genericType: " + Arrays.toString(itemField.getType().getTypeParameters()));

        final Deque<Class<?>> pTypes = ReflectionUtils.getParameterizedTypes(itemField);

        final Object itemInstance = ReflectionUtils.instantiate(itemField.getType());
        final Field fooValueField = ReflectionUtils.getField(FooContainer.Foo.class, "fooValue");
        ReflectionUtils.setField(itemInstance, fooValueField, "test-foo-value");
        ReflectionUtils.setField(container, itemField, itemInstance);

        System.out.println(container);
        assertThat(container.getItem().getFooValue()).isInstanceOf(String.class);
    }

    @Test
    void fooBarBazContainer() throws Exception {

        final Field itemField = ReflectionUtils.getField(FooBarBazContainer.class, "item");
        final Deque<Class<?>> pTypes = ReflectionUtils.getParameterizedTypes(itemField.getGenericType());

        Class<?> parentClass = FooBarBazContainer.class;
        Object parentInstance = ReflectionUtils.instantiate(parentClass);
        final Object result = parentInstance;
        while (!pTypes.isEmpty()) {
            final Class<?> currClass = pTypes.pollFirst();

            System.out.println("cur: " + currClass.getSimpleName()
                    + ", parent: " + parentClass.getSimpleName());

            final Object instance = ReflectionUtils.instantiate(currClass);

//            final Set<Field> fields = org.reflections.ReflectionUtils.getFields(parentClass,
//                    f -> f.getGenericType().equals(((Type) parentClass).get)
//
//            )

//            if (!fields.isEmpty()) {
//                ReflectionUtils.setField(parentInstance, fields.iterator().next(), instance);
//            }
            //ReflectionUtils.setField();
//            }

            for (Field f : org.reflections.ReflectionUtils.getFields(parentClass)) {
                System.out.println("  field: " + f.getName() + ", type: " + f.getType() + ", gen type: " + f.getGenericType());
            }

            parentInstance = instance;
            parentClass = currClass;

            //System.out.println(parentInstance);
            System.out.println();
        }


        System.out.println("Result: " + result);
    }

    @Test
    void listContainer() throws Exception {
//        Container nested = new Container();
//        final List<Foo<Bar<Baz<String>>>> listOfFoo = nested.getContainer();
//
//        final Foo<Bar<Baz<String>>> foo = new Foo<>();
//        listOfFoo.add(foo);
//
//        final Bar<Baz<String>> bar = new Bar<>();
//        foo.getFooList().add(bar);
//
//        final Baz<String> baz = new Baz<>();
//        bar.getBarList().add(baz);
//
//        baz.getBazItems().add("A value!");

        //System.out.println(nested);
        //==========

//    class Container {
//        private List<Foo<Bar<Baz<String>>>> Container = new ArrayList<>();
//    }
//    class Foo<T> {
//        private List<T> fooList = new ArrayList<>();
//    }
//    class Bar<T> {
//        private List<T> barList = new ArrayList<>();
//    }
//    class Baz<T> {
//        private List<T> bazList = new ArrayList<>();
//    }
        final Class<?> rootClass = ListOfOuterMidInnerString.class;
        final ListOfOuterMidInnerString obj = ReflectionUtils.instantiate(ListOfOuterMidInnerString.class);

        // create: List<Foo<Bar<Baz<String>>>> listOfFooBarBaz
        final Field rootField = rootClass.getDeclaredField("listOfFooBarBaz");
        ReflectionUtils.setField(obj, rootField, new ArrayList<>());

        final Type rootType = rootField.getGenericType();
        System.out.println("rootType: " + rootType);

        // Create: fooList; add foo
        final ParameterizedType type1 = (ParameterizedType) rootType;
        Class<?> type1Class = (Class<?>) type1.getRawType();
        System.out.println("type1: " + type1 + ", " + type1.getActualTypeArguments()[0]);
        System.out.println("type1: " + type1Class);
        Object foo = ReflectionUtils.instantiate(type1Class);
        final Field fooListField = ReflectionUtils.getField(foo.getClass(), "fooList");

        final ParameterizedType type2 = (ParameterizedType) type1.getActualTypeArguments()[0];
        Class<?> type2Class = (Class<?>) type2.getRawType();
        Object bar = ReflectionUtils.instantiate(type2Class);
        final Field barListField = ReflectionUtils.getField(bar.getClass(), "barList");
        ReflectionUtils.setField(foo, fooListField, new ArrayList<>());

        System.out.println("type2: " + type2 + ", " + type2.getActualTypeArguments()[0]);
        System.out.println("type2: " + type2Class);

        ((List) fooListField.get(obj)).add(foo);


        final ParameterizedType type3 = (ParameterizedType) type2.getActualTypeArguments()[0];
        Class<?> type3Class = (Class<?>) type3.getRawType();
        Object baz = ReflectionUtils.instantiate(type3Class);
        final Field bazListField = ReflectionUtils.getField(baz.getClass(), "barList");
        ReflectionUtils.setField(baz, bazListField, new ArrayList<>());
        System.out.println("type3: " + type3 + ", " + type3.getActualTypeArguments()[0]);
        System.out.println("type3: " + type3Class);

        ((List) barListField.get(bar)).add(bar);

        final ParameterizedType type4 = (ParameterizedType) type3.getActualTypeArguments()[0];
        Class<?> type4Class = (Class<?>) type4.getRawType();
//        Object baz = ReflectionUtils.instantiate(type4Class);
//        final Field bazListField = ReflectionUtils.getField(baz.getClass(), "bazList");
//        ReflectionUtils.setField(baz, bazListField, new ArrayList<>());
//        System.out.println("type4: " + type4 + ", " + type4.getActualTypeArguments()[0]);
//        System.out.println("type4: " + type4Class);

        //((List) bazListField.get(baz)).add(baz);

//        ValueGenerator gen = new GeneratorMap().get((Class<?>) type4.getActualTypeArguments()[0]);
//        ((List) bazListField.get(baz)).add(gen.generate());
        //((List) bazListField.get(baz)).add("test");


        System.out.println(obj);

    }

    @Test
    void getParameterizedTypesByClassAndFieldName() {
        assertThat(getParameterizedTypes(Address.class, "phoneNumbers"))
                .containsExactly(Phone.class);

        assertThat(getParameterizedTypes(FooBarBazContainer.class, "item"))
                .containsExactly(
                        Bar.class,
                        Baz.class,
                        String.class);

        assertThat(getParameterizedTypes(ListOfOuterMidInnerString.class, "listOfFooBarBaz"))
                .containsExactly(
                        Outer.class,
                        Mid.class,
                        Inner.class,
                        String.class);
    }

    private Queue<Class<?>> getParameterizedTypes(Class<?> klass, String fieldName) {
        final Field field = ReflectionUtils.getField(klass, fieldName);
        return ReflectionUtils.getParameterizedTypes(field.getGenericType());
    }

    @Test
    void getTypeMap() {
        //List<Foo<List<Bar<List<Baz<List<String>>>>>>> listOfFoo_ListOfBar_ListOfBaz_ListOfString
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "listOfFoo_ListOfBar_ListOfBaz_ListOfString")))
                // XXX --- can't map
                .containsEntry(GenericType.with(List.class, "E"), Foo.class)
                .containsEntry(GenericType.with(List.class, "E"), Bar.class)
                .containsEntry(GenericType.with(List.class, "E"), Baz.class)
                .containsEntry(GenericType.with(List.class, "E"), String.class)
                // XXX --- List.E maps to multiple types
                .containsEntry(GenericType.with(Foo.class, "X"), List.class)
                .containsEntry(GenericType.with(Bar.class, "Y"), List.class)
                .containsEntry(GenericType.with(Baz.class, "Z"), List.class)
                .hasSize(7);

        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "fooBarBazString")))
                .containsEntry(GenericType.with(Foo.class, "X"), Bar.class)
                .containsEntry(GenericType.with(Bar.class, "Y"), Baz.class)
                .containsEntry(GenericType.with(Baz.class, "Z"), String.class)
                .hasSize(3);

        // NOTE the map structure in its current form cannot support nested types like "Pair<Long, Pair<Integer, String>>"
        // because the keys are the same and the nested Pair bindings overwrites values of the outer Pair bindings

        // Pair<Long, Pair<Integer, String>> -- FIXME
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "pairLongPairIntegerString")))
                .containsEntry(GenericType.with(Pair.class, "L"), null)
                .containsEntry(GenericType.with(Pair.class, "R"), Pair.class)
                .hasSize(2);

        // Pair<A, Pair<Integer, String>> -- FIXME
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "pairAPairIntegerString")))
                .containsEntry(GenericType.with(Pair.class, "L"), null)
                .containsEntry(GenericType.with(Pair.class, "R"), Pair.class)
                .hasSize(2);

        // int[]
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "arrayOfInts")))
                .isEmpty();

        // C[] - FIXME
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "arrayOfCs")))
                .containsEntry(GenericType.with(Array.class, "C"), null)
                .hasSize(1);

        // Triplet<A, Foo<Bar<Baz<String>>>, List<C>>
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "tripletA_FooBarBazString_ListOfC")))
                .containsEntry(GenericType.with(Triplet.class, "M"), null)
                .containsEntry(GenericType.with(Triplet.class, "N"), Foo.class)
                .containsEntry(GenericType.with(Triplet.class, "O"), List.class)
                .containsEntry(GenericType.with(Foo.class, "X"), Bar.class)
                .containsEntry(GenericType.with(Bar.class, "Y"), Baz.class)
                .containsEntry(GenericType.with(Baz.class, "Z"), String.class)
                .containsEntry(GenericType.with(List.class, "E"), null)
                .hasSize(7);

        // Pair<A, Foo<Bar<B>>> pairAFooBarB;
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "pairAFooBarB")))
                .containsEntry(GenericType.with(Pair.class, "L"), null)
                .containsEntry(GenericType.with(Pair.class, "R"), Foo.class)
                .containsEntry(GenericType.with(Foo.class, "X"), Bar.class)
                .containsEntry(GenericType.with(Bar.class, "Y"), null)
                .hasSize(4);

        // List<Baz<String>>
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "listOfBazStrings")))
                .containsEntry(GenericType.with(List.class, "E"), Baz.class)
                .containsEntry(GenericType.with(Baz.class, "Z"), String.class)
                .hasSize(2);

        // Foo<Bar<Pair<B, C>>>
        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "fooBarPairBC")))
                .containsEntry(GenericType.with(Foo.class, "X"), Bar.class)
                .containsEntry(GenericType.with(Bar.class, "Y"), Pair.class)
                .containsEntry(GenericType.with(Pair.class, "L"), null)
                .containsEntry(GenericType.with(Pair.class, "R"), null)
                .hasSize(4);

        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "pairFooBarStringB")))
                .containsEntry(GenericType.with(Pair.class, "L"), Foo.class)
                .containsEntry(GenericType.with(Foo.class, "X"), Bar.class)
                .containsEntry(GenericType.with(Bar.class, "Y"), String.class)
                .containsEntry(GenericType.with(Pair.class, "R"), null)
                .hasSize(4);

        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(MiscFields.class, "pairAB")))
                .containsEntry(GenericType.with(Pair.class, "L"), null)
                .containsEntry(GenericType.with(Pair.class, "R"), null)
                .hasSize(2);

        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(FooBarBazContainer.class, "item")))
                .containsEntry(GenericType.with(Foo.class, "X"), Bar.class)
                .containsEntry(GenericType.with(Bar.class, "Y"), Baz.class)
                .containsEntry(GenericType.with(Baz.class, "Z"), String.class)
                .hasSize(3);

        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(ListOfOuterMidInnerString.class, "items")))
                .containsEntry(GenericType.with(List.class, "E"), Outer.class)
                .containsEntry(GenericType.with(Outer.class, "T"), Mid.class)
                .containsEntry(GenericType.with(Mid.class, "T"), Inner.class)
                .containsEntry(GenericType.with(Inner.class, "T"), String.class)
                .hasSize(4);

        assertThat(ReflectionUtils.getTypeMap(ReflectionUtils.getField(Person.class, "address")))
                .isEmpty();
        ;
    }

    @Test
    void getField() {
        assertThat(ReflectionUtils.getField(Person.class, "name").getName()).isEqualTo("name");
        assertThat(ReflectionUtils.getField(Person.class, "address").getName()).isEqualTo("address");
        assertThat(ReflectionUtils.getField(Person.class, "address.city").getName()).isEqualTo("city");
        assertThat(ReflectionUtils.getField(Person.class, "address.phoneNumbers").getName()).isEqualTo("phoneNumbers");
        assertThat(ReflectionUtils.getField(Person.class, "pets").getName()).isEqualTo("pets");
        assertThat(ReflectionUtils.getField(Person.class, "age").getName()).isEqualTo("age");
        assertThat(ReflectionUtils.getField(Person.class, "gender").getName()).isEqualTo("gender");
        assertThat(ReflectionUtils.getField(Person.class, "address.phoneNumbers").getName()).isEqualTo("phoneNumbers");

        assertThat(ReflectionUtils.getField(Address.class, "phoneNumbers").getName()).isEqualTo("phoneNumbers");
        assertThat(ReflectionUtils.getField(Phone.class, "countryCode").getName()).isEqualTo("countryCode");
    }

    @Test
    void invalidFieldPath() {
        assertThatThrownBy(() -> ReflectionUtils.getField(Address.class, "phoneNumbers.countryCode"))
                .isInstanceOf(RuntimeException.class)
                // TODO  better error message
                .hasMessage("Error getting field 'phoneNumbers.countryCode' from class org.stubber.pojo.Address");
    }
}
