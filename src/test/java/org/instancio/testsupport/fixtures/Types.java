package org.instancio.testsupport.fixtures;

import org.instancio.TypeToken;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.basic.Triplet;
import org.instancio.pojo.generics.foobarbaz.Foo;

import java.util.List;
import java.util.Map;

public class Types {

    public static final TypeToken<?> STRING = new TypeToken<String>() {
    };

    public static final TypeToken<?> INTEGER = new TypeToken<Integer>() {
    };

    public static final TypeToken<?> LIST_STRING = new TypeToken<List<String>>() {
    };

    public static final TypeToken<?> LIST_ITEM_STRING = new TypeToken<List<Item<String>>>() {
    };

    public static final TypeToken<?> LIST_LIST_STRING = new TypeToken<List<List<String>>>() {
    };

    public static final TypeToken<?> MAP_STRING_BOOLEAN = new TypeToken<Map<String, Boolean>>() {
    };

    public static final TypeToken<?> MAP_INTEGER_STRING = new TypeToken<Map<Integer, String>>() {
    };

    public static final TypeToken<?> ITEM_STRING = new TypeToken<Item<String>>() {
    };

    public static final TypeToken<?> PAIR_INTEGER_STRING = new TypeToken<Pair<Integer, String>>() {
    };

    public static final TypeToken<?> TRIPLET_BOOLEAN_INTEGER_STRING = new TypeToken<Triplet<Boolean, Integer, String>>() {
    };

    public static final TypeToken<?> FOO_LIST_INTEGER = new TypeToken<Foo<List<Integer>>>() {
    };

    private Types() {
        // non-instantiable
    }
}
