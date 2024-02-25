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
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;

import static org.instancio.Select.all;

class SubtypeMismatchDueToCustomGeneratorErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        class Container {
            StringHolderInterface holderOne;
            StringHolderInterface holderTwo;
        }

        // 1) subtype all(StringHolderInterface) to StringHolder
        // 2) but in custom generator initialise one StringHolderInterface to StringHolderAlternativeImpl
        //
        // Instancio fails checking field value because
        // it expects StringHolder but gets StringHolderAlternativeImpl
        //
        Instancio.of(Container.class)
                .subtype(all(StringHolderInterface.class), StringHolder.class)
                .supply(all(Container.class), random -> {
                    Container container = new Container();
                    container.holderOne = new StringHolderAlternativeImpl("foo");
                    container.holderTwo = null;
                    return container;
                }).create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.SubtypeMismatchDueToCustomGeneratorErrorMessageTest.methodUnderTest(SubtypeMismatchDueToCustomGeneratorErrorMessageTest.java:47)

                Reason: unable to get value from field.

                 -> Field........: private java.lang.String org.instancio.test.support.pojo.basic.StringHolder.value
                 -> Target type..: class org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl

                If you think this is a bug, please submit a bug report including the stacktrace:
                https://github.com/instancio/instancio/issues

                """;
    }
}
