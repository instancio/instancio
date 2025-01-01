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
import org.instancio.Model;
import org.instancio.test.support.pojo.basic.StringHolder;

import java.util.List;

import static org.instancio.Select.allStrings;

class SetModelIncompatibleSelectorTargetErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Model<List<Integer>> model = Instancio.ofList(Integer.class).toModel();

        Instancio.of(StringHolder.class)
                .setModel(allStrings(), model)
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SetModelIncompatibleSelectorTargetErrorMessageTest.methodUnderTest(SetModelIncompatibleSelectorTargetErrorMessageTest.java:34)

                Reason: Model<List<Integer>> specified in setModel() method is incompatible with the selector target

                 -> Model type ............: List<Integer>
                 -> Selector target type ..: String

                """;
    }

}
