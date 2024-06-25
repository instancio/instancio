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
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;

class SchemaInvalidSpecMethodNameTest extends AbstractErrorMessageTestTemplate {

    @SchemaResource(data = "id, value\n1, value")
    private interface SampleSchema extends Schema {
        SchemaSpec<Integer> invalidName();
    }

    @Override
    void methodUnderTest() {
        Instancio.createSchema(SampleSchema.class)
                .invalidName()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SchemaInvalidSpecMethodNameTest.methodUnderTest(SchemaInvalidSpecMethodNameTest.java:34)

                Reason: unmatched spec method 'invalidName()' declared by the schema class

                 -> org.external.errorhandling.SchemaInvalidSpecMethodNameTest$SampleSchema

                The property name 'invalidName' does not map to any property in the data:

                 -> [id, value]

                """;
    }

}
