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
import org.instancio.generator.ValueSpec;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;

class SchemaSpecMethodInvalidReturnTypeTest extends AbstractErrorMessageTestTemplate {

    @SchemaResource(data = "x\n123")
    private interface SampleSchema extends Schema {

        /**
         * Wrong return type (should be {@link ValueSpec}).
         */
        Integer specMethodWithInvalidReturnType();
    }

    @Override
    void methodUnderTest() {
        Instancio.createSchema(SampleSchema.class)
                .specMethodWithInvalidReturnType();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SchemaSpecMethodInvalidReturnTypeTest.methodUnderTest(SchemaSpecMethodInvalidReturnTypeTest.java:37)

                Reason: invalid spec method 'specMethodWithInvalidReturnType()' declared by the schema class

                 -> org.external.errorhandling.SchemaSpecMethodInvalidReturnTypeTest$SampleSchema

                  │ Integer specMethodWithInvalidReturnType() {
                  │     ...
                  │ }

                To resolve this error:

                 -> Ensure the spec method returns a SchemaSpec<T>
                    (where T is not a parameterized type)

                    // Example
                    SchemaSpec<Integer> age();

                """;
    }

}
