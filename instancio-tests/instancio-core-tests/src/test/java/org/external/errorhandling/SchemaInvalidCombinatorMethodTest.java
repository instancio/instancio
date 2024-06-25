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

class SchemaInvalidCombinatorMethodTest extends AbstractErrorMessageTestTemplate {

    /**
     * A provider without a {@link CombinatorMethod}.
     */
    private static class EmptyCombinatorProvider implements CombinatorProvider {}

    @SchemaResource(data = "x,y\n1,2")
    interface SampleSchema extends Schema {

        SchemaSpec<Integer> x();

        SchemaSpec<Integer> y();

        @DerivedSpec(fromSpec = {"x", "y"}, by = EmptyCombinatorProvider.class)
        SchemaSpec<Integer> xAndY();
    }

    @Override
    void methodUnderTest() {
        Instancio.createSchema(SampleSchema.class)
                .xAndY()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SchemaInvalidCombinatorMethodTest.methodUnderTest(SchemaInvalidCombinatorMethodTest.java:48)

                Reason: could not resolve @CombinatorMethod for spec method 'xAndY()' declared by the schema class

                 -> org.external.errorhandling.SchemaInvalidCombinatorMethodTest$SampleSchema

                CombinatorProvider implementation:

                 -> org.external.errorhandling.SchemaInvalidCombinatorMethodTest$EmptyCombinatorProvider

                To resolve this error:

                 -> Ensure the CombinatorProvider implementation contains
                    at least one method annotated with @CombinatorMethod.

                 -> If the CombinatorProvider contains more than one @CombinatorMethod
                    specify the method name in the '@DerivedSpec.method' attribute.

                 -> Ensure the @CombinatorMethod has an argument for every property name
                    specified in the '@DerivedSpec.fromSpec' attribute.

                """;
    }

}
