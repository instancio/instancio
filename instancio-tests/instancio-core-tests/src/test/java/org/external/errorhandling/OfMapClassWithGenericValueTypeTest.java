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
package org.external.errorhandling;

import org.instancio.Instancio;
import org.instancio.test.support.pojo.generics.basic.Item;

class OfMapClassWithGenericValueTypeTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.ofMap(String.class, Item.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.OfMapClassWithGenericValueTypeTest.methodUnderTest(OfMapClassWithGenericValueTypeTest.java:25)

                Reason: invalid usage of ofMap() method

                  -> The argument Item<K> is a generic class and also requires type parameter(s),
                     but this method does not support nested generics.

                To resolve this error:

                 -> Use one of the methods that accepts a type token, e.g:

                    Map<Item<String>, Pair<String, Integer>> map = Instancio.ofMap(
                                    new TypeToken<Item<String>>() {},
                                    new TypeToken<Pair<String, Integer>>() {})
                            // additional API methods...
                            .create();
                    or:

                    Map<Item<String>, Pair<String, Integer>> map = Instancio.of(new TypeToken<Map<Item<String>, Pair<String, Integer>>>(){})
                            // additional API methods...
                            .create();

                """;
    }

}
