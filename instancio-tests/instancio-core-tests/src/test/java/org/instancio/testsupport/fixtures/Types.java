/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.testsupport.fixtures;

import org.instancio.TypeToken;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;

import java.util.List;
import java.util.Map;

public class Types {

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
