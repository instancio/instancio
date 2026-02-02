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

import org.instancio.GeneratorSpecProvider;
import org.instancio.Instancio;
import org.instancio.test.support.pojo.person.Person;

import static org.instancio.Select.allInts;

class GenerateNullGeneratorSpecProviderTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .generate(allInts(), (GeneratorSpecProvider<?>) null);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.GenerateNullGeneratorSpecProviderTest.methodUnderTest(GenerateNullGeneratorSpecProviderTest.java:29)

                Reason: the second argument of 'generate()' method must not be null
                 -> To generate a null value, use 'set(TargetSelector, null)'

                	Example:
                	Person person = Instancio.of(Person.class)
                		.set(field("firstName"), null)
                		.create();

                """;
    }

}
