/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.test.support.pojo.generics.basic.Pair;

class OfSetClassWithGenericTypeTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.ofSet(Pair.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.OfSetClassWithGenericTypeTest.methodUnderTest(OfSetClassWithGenericTypeTest.java:25)

                Reason: invalid usage of ofSet() method

                  -> The argument Pair<L, R> is a generic class and also requires type parameter(s),
                     but this method does not support nested generics.

                To resolve this error:

                 -> Use one of the methods that accepts a type token, e.g:

                    List<Pair<String, Integer>> list = Instancio.ofList(new TypeToken<Pair<String, Integer>>() {})
                            // additional API methods...
                            .create();
                    or:

                    List<Pair<String, Integer>> list = Instancio.of(new TypeToken<List<Pair<String, Integer>>>(){})
                            // additional API methods...
                            .create();

                """;
    }

}
