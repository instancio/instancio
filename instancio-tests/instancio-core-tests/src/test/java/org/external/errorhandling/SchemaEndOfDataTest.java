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
import org.instancio.settings.Keys;
import org.instancio.settings.SchemaDataAccess;

class SchemaEndOfDataTest extends AbstractErrorMessageTestTemplate {

    @SchemaResource(data = "id\n1")
    private interface SampleSchema extends Schema {}

    @Override
    void methodUnderTest() {
        final SampleSchema schema = Instancio.ofSchema(SampleSchema.class)
                .withSetting(Keys.SCHEMA_DATA_ACCESS, SchemaDataAccess.SEQUENTIAL)
                .create();

        schema.stringSpec("id").get(); // 1
        schema.stringSpec("id").get(); // error
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SchemaEndOfDataTest.methodUnderTest(SchemaEndOfDataTest.java:36)

                Reason: reached end of data for schema class

                 -> org.external.errorhandling.SchemaEndOfDataTest$SampleSchema

                The error was triggered because of the following settings:

                 -> Keys.SCHEMA_DATA_ACCESS ........: SchemaDataAccess.SEQUENTIAL
                 -> Keys.SCHEMA_DATA_END_STRATEGY ..: SchemaDataEndStrategy.FAIL

                To resolve this error:

                 -> Ensure the data source provides a sufficient number of records
                 -> Set Keys.SCHEMA_DATA_END_STRATEGY to SchemaDataEndStrategy.RECYCLE
                 -> Set Keys.SCHEMA_DATA_ACCESS to SchemaDataAccess.RANDOM

                Note that the last two options may result in duplicate values being produced.

                """;
    }

}
