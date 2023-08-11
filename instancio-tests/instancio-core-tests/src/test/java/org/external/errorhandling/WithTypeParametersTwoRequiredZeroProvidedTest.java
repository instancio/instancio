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
import org.instancio.test.support.pojo.generics.container.ItemContainer;

class WithTypeParametersTwoRequiredZeroProvidedTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.create(ItemContainer.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.WithTypeParametersTwoRequiredZeroProvidedTest.methodUnderTest(WithTypeParametersTwoRequiredZeroProvidedTest.java:25)

                Reason: incorrect type parameters specified

                 -> Class org.instancio.test.support.pojo.generics.container.ItemContainer requires 2 type parameter(s): [X, Y]
                 -> The number of parameters provided was 0

                To resolve this error:

                 -> Specify the correct number of parameters, e.g.

                    Instancio.of(Map.class).
                        .withTypeParameters(UUID.class, Person.class)
                        .create();

                 -> Or use a type token:

                    Instancio.create(new TypeToken<Map<UUID, Person>>() {});

                """;
    }

}
