/*
 * Copyright 2022-2026 the original author or authors.
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
import org.instancio.test.support.pojo.person.Person;

class SeedNotSupportedWithToModelErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .withSeed(123)
                .toModel();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SeedNotSupportedWithToModelErrorMessageTest.methodUnderTest(SeedNotSupportedWithToModelErrorMessageTest.java:27)

                Reason: withSeed() cannot be used when creating a Model via toModel()

                A seed specified via withSeed() applies only to the builder chain it is
                called on. It is an execution-time parameter and is not part of a Model.

                To resolve this error, choose one of the following:

                 -> Remove the withSeed() call

                 -> If the seed should be part of the Model, specify it via Settings instead:

                    Model<Example> model = Instancio.of(Example.class)
                        .withSetting(Keys.SEED, 123L)
                        .toModel();

                """;
    }
}
