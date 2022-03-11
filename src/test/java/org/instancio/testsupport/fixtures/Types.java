package org.instancio.testsupport.fixtures;

import org.instancio.pojo.generics.container.GenericItem;
import org.instancio.pojo.generics.container.Triplet;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;
import org.instancio.testsupport.TypeReference;

import java.util.List;
import java.util.Map;

public class Types {

    public static final TypeReference<?> STRING = new TypeReference<String>() {
    };

    public static final TypeReference<?> INTEGER = new TypeReference<Integer>() {
    };

    public static final TypeReference<?> LIST_STRING = new TypeReference<List<String>>() {
    };

    public static final TypeReference<?> LIST_LIST_STRING = new TypeReference<List<List<String>>>() {
    };

    public static final TypeReference<?> MAP_STRING_BOOLEAN = new TypeReference<Map<String, Boolean>>() {
    };

    public static final TypeReference<?> MAP_INTEGER_STRING = new TypeReference<Map<Integer, String>>() {
    };

    public static final TypeReference<?> TRIPLET_BOOLEAN_INTEGER_STRING = new TypeReference<Triplet<Boolean, Integer, String>>() {
    };

    public static final TypeReference<?> GENERIC_ITEM_STRING = new TypeReference<GenericItem<String>>() {
    };

    public static final TypeReference<?> FOO_LIST_INTEGER = new TypeReference<Foo<List<Integer>>>() {
    };

    private Types() {
        // non-instantiable
    }
}
