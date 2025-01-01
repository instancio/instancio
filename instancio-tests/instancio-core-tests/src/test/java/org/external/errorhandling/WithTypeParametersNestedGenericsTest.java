/*
 * Copyright 2022-2025 the original author or authors.
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

import java.util.List;
import java.util.Map;

class WithTypeParametersNestedGenericsTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Map.class)
                .withTypeParameters(String.class, List.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.WithTypeParametersNestedGenericsTest.methodUnderTest(WithTypeParametersNestedGenericsTest.java:28)

                Reason: incorrect type parameters specified

                  -> The argument List<E> is a generic class and also requires type parameter(s),
                     but this method does not support nested generics.

                To resolve this error:

                 -> Use a type token, e.g:

                    Map<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});

                    or the builder version:

                    Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){})
                            // additional API methods...
                            .create();

                """;
    }

}
