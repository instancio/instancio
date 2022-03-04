package org.instancio.util;

import org.instancio.pojo.generics.FooContainer;
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
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReflectionUtilsTest {

    @Test
    void getDeclaredAndSuperclassFields() {
        List<Field> results = ReflectionUtils.getDeclaredAndSuperclassFields(Inheritance.ChildClass.class);

        assertThat(results.stream().map(Field::getName))
                .containsExactly("childField", "privateBaseField", "protectedBaseField");
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
