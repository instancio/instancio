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
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import static org.instancio.Select.field;

@FeatureTag(Feature.ON_COMPLETE)
class OnCompleteTypeMismatchErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.of(StringHolder.class)
                .onComplete(field("value"), (Integer wrongType) -> {
                }).create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.OnCompleteTypeMismatchErrorMessageTest.methodUnderTest(OnCompleteTypeMismatchErrorMessageTest.java:32)

                Reason: onComplete() callback error.

                Node matched by the callback's selector:
                 -> Node[StringHolder.value, depth=1, type=String]

                ClassCastException was thrown by the callback.
                This usually happens because the type declared by the callback
                does not match the actual type of the target object.

                Example:
                onComplete(all(Foo.class), (Bar wrongType) -> {
                               ^^^^^^^^^    ^^^^^^^^^^^^^
                })

                Caused by:
                class java.lang.String cannot be cast to class java.lang.Integer (java.lang.String and java.lang.Integer are in module java.base of loader 'bootstrap')

                """;
    }
}
