/*
 * Copyright 2022-2023 the original author or authors.
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
import org.instancio.TypeToken;
import org.instancio.exception.InstancioException;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.util.Set;

import static org.instancio.Select.all;

class ImpossibleCollectionSizeErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    protected Class<?> expectedException() {
        return InstancioException.class;
    }

    @Override
    void methodUnderTest() {
        Instancio.of(new TypeToken<Set<Boolean>>() {})
                .withSettings(Settings.create().set(Keys.FAIL_ON_ERROR, true))
                .generate(all(Set.class), gen -> gen.collection().size(100))
                .create();
    }

    @Override
    String expectedMessage() {
        return """
                Internal error occurred creating an object.

                Internal errors are suppressed by default and
                can be ignored if not applicable to the current test
                 -> at org.external.errorhandling.ImpossibleCollectionSizeErrorMessageTest.methodUnderTest(ImpossibleCollectionSizeErrorMessageTest.java:40)

                Reason: unable to populate Collection of size 100: class Set<Boolean> (depth=0)

                 │ Path to root:
                 │   <0:Set>   <-- Root
                 │
                 │ Format: <depth:class: field>


                Could not generate enough elements to populate the collection.
                This typically occurs with Sets when the number of potential values is
                limited and the target set cannot be generated due to duplicate values
                being generated (for example, when the element type is an enum).

                 -> Target size: 100

                    The size was either chosen randomly based on current settings,
                    or may have been specified explicitly via the API.

                 -> Current size settings are

                    Keys.COLLECTION_MIN_SIZE: 2
                    Keys.COLLECTION_MAX_SIZE: 6


                """;
    }
}
