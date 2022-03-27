/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.bulk;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.pojo.generics.basic.Item;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;
import org.instancio.pojo.person.Person;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Fail.fail;
import static org.instancio.testsupport.asserts.ReflectionAssert.assertThatObject;
import static org.instancio.testsupport.utils.TypeUtils.shortenPackageNames;

/**
 * Various contrived generic types.
 */
class TypeTokenBulkAutoVerificationTest {
    private static final Logger LOG = LoggerFactory.getLogger(TypeTokenBulkAutoVerificationTest.class);

    @Test
    void arrays() {
        bulkAssertFullyPopulated(
                new TypeToken<Item<Integer>[]>() {},
                new TypeToken<Pair<String, int[]>>() {},
                new TypeToken<Person>() {},
                new TypeToken<List<int[]>>() {});
    }

    @Test
    void lists() {
        bulkAssertFullyPopulated(
                new TypeToken<List<Item<Pair<Integer, String>>[]>>() {},
                new TypeToken<List<List<List<List<String>>>>>() {},
                new TypeToken<List<Map<Short, List<String>>>>() {},
                new TypeToken<List<Item<Item<List<String>>>>>() {});
    }

    @Test
    void maps() {
        bulkAssertFullyPopulated(
                new TypeToken<Map<String, Item<Integer>[]>>() {},
                new TypeToken<Map<Integer, Person>>() {},
                new TypeToken<Map<String, Map<Integer, String>>>() {},
                new TypeToken<Map<String, Map<Integer, List<String>>>>() {},
                new TypeToken<Map<Item<Map<Integer, String>>, String>>() {},
                new TypeToken<Map<Pair<Integer, String>, Pair<Map<String, Short>, Long>>>() {});
    }

    @Test
    void items() {
        bulkAssertFullyPopulated(
                new TypeToken<Item<Item<Integer>>>() {},
                new TypeToken<Item<Item<Item<Item<String>>>>>() {},
                new TypeToken<List<Item<List<Item<List<Item<String>>>>>>>() {});
    }

    @Test
    void pairs() {
        bulkAssertFullyPopulated(
                new TypeToken<Pair<Boolean, Pair<Byte, String>>>() {},
                new TypeToken<Pair<Boolean, Pair<Byte, Pair<Integer, String>>>>() {},
                new TypeToken<Pair<Pair<Byte, String>, Pair<Integer, String>>>() {});
    }

    @Test
    void fooBarBaz() {
        bulkAssertFullyPopulated(
                new TypeToken<Foo<Bar<Baz<Foo<Bar<Baz<String>>>>>>>() {},
                new TypeToken<Foo<Foo<Foo<Baz<Baz<Baz<Foo<Short>>>>>>>>() {});
    }

    private static void bulkAssertFullyPopulated(TypeToken<?>... typeTokens) {
        Map<Type, Object> failed = new HashMap<>();
        for (TypeToken<?> typeToken : typeTokens) {
            final Object result = Instancio.create(typeToken);

            try {
                assertThatObject(result)
                        .as("Type '%s' failed: %s", typeToken.get().getTypeName(), result)
                        .isFullyPopulated();
            } catch (AssertionError e) {
                failed.put(typeToken.get(), result);
            }
        }

        if (!failed.isEmpty()) {
            LOG.error("Failures:");
            failed.forEach((type, obj) -> {
                LOG.error("\n\n-> '{}': {}", shortenPackageNames(type), obj);
            });

            fail("Number of failures: %s", failed.size());
        }
    }

}
