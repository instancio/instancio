/*
 * Copyright 2022-2024 the original author or authors.
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

import java.util.List;

import static org.instancio.Select.all;
import static org.instancio.Select.scope;

class TypeMismatchWithNestedCollectionTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(new TypeToken<List<List<String>>>() {})
                .set(all(List.class).within(scope(List.class), scope(List.class)), "invalid arg")
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.TypeMismatchWithNestedCollectionTest.methodUnderTest(TypeMismatchWithNestedCollectionTest.java:32)

                Reason: error assigning value to: class List<String> (depth=1)

                 │ Path to root:
                 │   <1:ArrayList>
                 │    └──<0:ArrayList>   <-- Root
                 │
                 │ Format: <depth:class: field>


                Type mismatch:

                 -> Target type ..............: List<String>
                 -> Provided argument type ...: String
                 -> Provided argument value ..: "invalid arg"

                """;
    }
}
