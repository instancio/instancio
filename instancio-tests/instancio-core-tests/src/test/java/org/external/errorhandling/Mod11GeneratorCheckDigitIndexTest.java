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
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import static org.instancio.Select.root;

@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR})
class Mod11GeneratorCheckDigitIndexTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(String.class)
                .generate(root(), gen -> gen.checksum().mod11()
                        .startIndex(3)
                        .endIndex(5)
                        .checkDigitIndex(4))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.Mod11GeneratorCheckDigitIndexTest.methodUnderTest(Mod11GeneratorCheckDigitIndexTest.java:34)

                Reason: checkDigitIndex must satisfy condition:
                  ->  checkDigitIndex >= 0 && (checkDigitIndex < startIndex || checkDigitIndex >= endIndex)

                Actual values were:
                  -> startIndex .......: 3
                  -> endIndex .........: 5
                  -> checkDigitIndex ..: 4


                """;
    }
}
