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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

class SchemaCombinatorMethodIllegalArgumentsTest extends AbstractErrorMessageTestTemplate {

    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnotherAnnotation {}

    private static class SampleCombinator implements CombinatorProvider {
        @AnotherAnnotation
        @CombinatorMethod
        List<Integer> processX(String x, Long l, List<String> z) {
            return null;
        }
    }

    @SchemaResource(data = "x\n1")
    private interface SampleSchema extends Schema {
        SchemaSpec<Integer> x();

        @DerivedSpec(fromSpec = {"x"}, by = SampleCombinator.class)
        SchemaSpec<Integer> fromX();
    }

    @Override
    void methodUnderTest() {
        Instancio.createSchema(SampleSchema.class)
                .fromX()
                .get();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SchemaCombinatorMethodIllegalArgumentsTest.methodUnderTest(SchemaCombinatorMethodIllegalArgumentsTest.java:58)

                Reason: exception thrown by spec method 'fromX()' declared by the schema class:

                 -> org.external.errorhandling.SchemaCombinatorMethodIllegalArgumentsTest$SampleSchema

                The error was caused by calling the method declared by SchemaCombinatorMethodIllegalArgumentsTest$SampleCombinator:

                  │ @SchemaCombinatorMethodIllegalArgumentsTest$AnotherAnnotation
                  │ @CombinatorMethod
                  │ List<Integer> processX(String, Long, List) {
                  │     ...
                  │ }

                Root cause: java.lang.IllegalArgumentException: wrong number of arguments: 1 expected: 3

                To resolve this error:

                 -> Check that the spec properties specified in '@DerivedProperty.fromSpec' attribute
                    match the number of arguments and parameter types defined by the @CombinatorMethod

                """;
    }

}
