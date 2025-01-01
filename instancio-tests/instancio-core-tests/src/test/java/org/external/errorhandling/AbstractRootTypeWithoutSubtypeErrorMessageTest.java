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
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;

class AbstractRootTypeWithoutSubtypeErrorMessageTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.create(StringHolderInterface.class);
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.AbstractRootTypeWithoutSubtypeErrorMessageTest.methodUnderTest(AbstractRootTypeWithoutSubtypeErrorMessageTest.java:25)

                Reason: could not create an instance of interface org.instancio.test.support.pojo.interfaces.StringHolderInterface

                Cause:

                 -> It is an abstract class and no subtype was provided

                To resolve this error:

                 -> Specify the subtype using the builder API:

                    AbstractPojo pojo = Instancio.of(AbstractPojo.class)
                        .subtype(all(AbstractPojo.class), ConcretePojo.class)
                        .create();

                 -> Or alternatively, specify the subtype using Settings:

                    Settings settings = Settings.create()
                        .mapType(AbstractPojo.class, ConcretePojo.class);

                    AbstractPojo pojo = Instancio.of(AbstractPojo.class)
                        .withSettings(settings)
                        .create();

                For more information see: https://www.instancio.org/user-guide/#subtype-mapping

                """;
    }
}
