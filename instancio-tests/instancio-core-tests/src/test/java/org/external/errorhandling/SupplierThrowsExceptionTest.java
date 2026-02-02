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
import org.instancio.test.support.pojo.basic.StringHolder;

import static org.instancio.Select.allStrings;

class SupplierThrowsExceptionTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringHolder.class)
                .supply(allStrings(), () -> {
                    throw new RuntimeException("some error");
                }).create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SupplierThrowsExceptionTest.methodUnderTest(SupplierThrowsExceptionTest.java:30)

                Reason: exception thrown by a custom Generator or Supplier

                 -> Could not generate value for: field StringHolder.value (depth=1)

                    <1:StringHolder: String value>
                     └──<0:StringHolder>

                """;
    }
}
