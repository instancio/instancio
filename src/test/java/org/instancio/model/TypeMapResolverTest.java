package org.instancio.model;

import org.instancio.pojo.generics.PairAPairIntegerString;
import org.instancio.pojo.generics.PairLongPairIntegerString;
import org.instancio.pojo.generics.TripletAFooBarBazStringListOfB;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.basic.Triplet;
import org.instancio.pojo.person.Person;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.utils.TypeMapBuilder;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collections;
import java.util.Map;

import static org.instancio.testsupport.asserts.TypeMapResolverAssert.assertThatResolver;

@GenericsTag
class TypeMapResolverTest {

    public static final Map<TypeVariable<?>, Class<?>> EMPTY_ROOT_TYPE_MAP = Collections.emptyMap();

    @Test
    void personAge() {
        final Class<?> klass = Person.class;
        final String field = "age";
        final TypeMapResolver resolver = new TypeMapResolver(EMPTY_ROOT_TYPE_MAP, getGenericType(klass, field));

        assertThatResolver(resolver).hasEmptyTypeMap();
    }

    @Test
    void pairAPairIntegerString() {
        final Class<?> klass = PairAPairIntegerString.class;
        final String field = "pairAPairIntegerString";

        final TypeMapResolver resolver = new TypeMapResolver(
                TypeMapBuilder.forClass(klass)
                        .with("A", Boolean.class)
                        .get(),
                getGenericType(klass, field));

        assertThatResolver(resolver)
                .hasTypeMapping(Pair.class, "L", Boolean.class)
                .hasTypeMapping(Pair.class, "R", "org.instancio.pojo.generics.basic." +
                        "Pair<java.lang.Integer, java.lang.String>")
                .hasTypeMapWithSize(2);
    }

    @Test
    void pairLongPairIntegerString() {
        final Class<?> klass = PairLongPairIntegerString.class;
        final String field = "pairLongPairIntegerString";

        final TypeMapResolver resolver = new TypeMapResolver(EMPTY_ROOT_TYPE_MAP, getGenericType(klass, field));

        assertThatResolver(resolver)
                .hasTypeMapping(Pair.class, "L", Long.class)
                .hasTypeMapping(Pair.class, "R", "org.instancio.pojo.generics.basic." +
                        "Pair<java.lang.Integer, java.lang.String>")
                .hasTypeMapWithSize(2);
    }

    @Test
    void tripletAFooBarBazStringListOfB() {
        final Class<?> klass = TripletAFooBarBazStringListOfB.class;
        final String field = "tripletA_FooBarBazString_ListOfB";

        final TypeMapResolver resolver = new TypeMapResolver(
                TypeMapBuilder.forClass(klass)
                        .with("A", Long.class)
                        .get(),
                getGenericType(klass, field));

        assertThatResolver(resolver)
                .hasTypeMapping(Triplet.class, "M", Long.class)
                .hasTypeMapping(Triplet.class, "N", "org.instancio.pojo.generics.foobarbaz." +
                        "Foo<org.instancio.pojo.generics.foobarbaz." +
                        "Bar<org.instancio.pojo.generics.foobarbaz." +
                        "Baz<java.lang." +
                        "String>>>")
                .hasTypeMapping(Triplet.class, "O", "java.util.List<B>")
                .hasTypeMapWithSize(3);
    }

    private static Type getGenericType(Class<?> klass, String fieldName) {
        return ReflectionUtils.getField(klass, fieldName).getGenericType();
    }

}
