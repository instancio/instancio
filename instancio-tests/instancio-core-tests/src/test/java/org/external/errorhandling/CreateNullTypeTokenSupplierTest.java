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
import org.instancio.TypeToken;

class CreateNullTypeTokenSupplierTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.create((TypeToken<?>) null);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.CreateNullTypeTokenSupplierTest.methodUnderTest(CreateNullTypeTokenSupplierTest.java:25)

                Reason: type token must not be null
                 -> Please provide a valid type token

                	Example:
                	Map<String, List<Integer>> map = Instancio.create(new TypeToken<Map<String, List<Integer>>>(){});

                	// or the builder version
                	Map<String, List<Integer>> map = Instancio.of(new TypeToken<Map<String, List<Integer>>>(){}).create();

                """;
    }
}
