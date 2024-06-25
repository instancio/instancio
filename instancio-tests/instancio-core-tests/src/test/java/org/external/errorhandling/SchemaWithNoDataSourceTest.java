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

class SchemaWithNoDataSourceTest extends AbstractErrorMessageTestTemplate {

    interface SampleSchema extends Schema {}

    @Override
    void methodUnderTest() {
        Instancio.createSchema(SampleSchema.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SchemaWithNoDataSourceTest.methodUnderTest(SchemaWithNoDataSourceTest.java:27)

                Reason: no data source provided for schema org.external.errorhandling.SchemaWithNoDataSourceTest$SampleSchema

                To resolve this error, please specify a data source using any of the options below.

                 -> Via the @SchemaResource annotation:

                    @SchemaResource(path = "path/to/data/sample.csv")
                    interface SampleSchema extends Schema { ... }

                 -> Using the schema builder API:

                    import org.instancio.schema.DataSource;

                    SampleSchema schema = Instancio.ofSchema(SampleSchema.class)
                        .withDataSource(myDataSource)
                        .create();

                """;
    }

}
