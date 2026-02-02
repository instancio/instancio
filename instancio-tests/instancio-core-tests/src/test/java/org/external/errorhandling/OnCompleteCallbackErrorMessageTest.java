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
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import static org.instancio.Select.field;

@FeatureTag(Feature.ON_COMPLETE)
class OnCompleteCallbackErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringHolder.class)
                .onComplete(field("value"), value -> {
                    throw new RuntimeException("expected error message");
                }).create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.OnCompleteCallbackErrorMessageTest.methodUnderTest(OnCompleteCallbackErrorMessageTest.java:33)

                Reason: onComplete() callback error.

                Node matched by the callback's selector:
                 -> Node[StringHolder.value, depth=1, type=String]

                Caused by:
                expected error message

                """;
    }
}
