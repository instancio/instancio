/*
 * Copyright 2022-2025 the original author or authors.
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

import java.util.function.Supplier;

import static org.instancio.Select.allInts;

class SupplyNullSupplierTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(Person.class)
                .supply(allInts(), (Supplier<?>) null);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SupplyNullSupplierTest.methodUnderTest(SupplyNullSupplierTest.java:30)

                Reason: null Supplier passed to 'supply()' method
                 -> To generate a null value, use 'set(TargetSelector, null)'

                	Example:
                	Person person = Instancio.of(Person.class)
                		.set(field("firstName"), null)
                		.create();

                """;
    }
}
