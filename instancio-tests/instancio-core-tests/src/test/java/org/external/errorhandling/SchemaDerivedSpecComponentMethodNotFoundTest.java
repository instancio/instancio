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
import org.instancio.schema.CombinatorMethod;
import org.instancio.schema.CombinatorProvider;
import org.instancio.schema.DerivedSpec;
import org.instancio.schema.Schema;
import org.instancio.schema.SchemaResource;
import org.instancio.schema.SchemaSpec;

class SchemaDerivedSpecComponentMethodNotFoundTest extends AbstractErrorMessageTestTemplate {

    @SchemaResource(data = "x\n123")
    private interface SampleSchema extends Schema {

        @DerivedSpec(fromSpec = "propertyDoesNotExist", by = Combinators.class)
        SchemaSpec<Integer> shouldFail();

        class Combinators implements CombinatorProvider {
            static @CombinatorMethod Integer sample(Integer any) {
                return -1;
            }
        }
    }

    @Override
    void methodUnderTest() {
        Instancio.createSchema(SampleSchema.class)
                .shouldFail()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SchemaDerivedSpecComponentMethodNotFoundTest.methodUnderTest(SchemaDerivedSpecComponentMethodNotFoundTest.java:45)

                Reason: invalid method name 'badName' specified by the `@DerivedSpec.fromSpec` attribute in schema class:

                 -> org.external.errorhandling.SchemaDerivedSpecComponentMethodNotFoundTest$SampleSchema

                To resolve this error:

                 -> Check the annotation attributes of the following method:

                  │ @DerivedSpec
                  │ SchemaSpec<Integer> shouldFail() {
                  │     ...
                  │ }

                """;
    }

}
