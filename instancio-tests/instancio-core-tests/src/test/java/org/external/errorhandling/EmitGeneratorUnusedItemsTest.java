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
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;

import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR})
class EmitGeneratorUnusedItemsTest extends AbstractErrorMessageTestTemplate {

    @Override
    void methodUnderTest() {
        Instancio.ofList(Integer.class)
                .size(1)
                .generate(all(Integer.class), gen -> gen.emit().items(-1, -2, -3))
                .create();
    }

    @Override
    String expectedMessage() {
        return """


                Error creating an object
                 -> at org.external.errorhandling.EmitGeneratorUnusedItemsTest.methodUnderTest(EmitGeneratorUnusedItemsTest.java:32)

                Reason: not all the items provided via the 'emit()' method have been consumed

                The following items are still remaining:

                 -> all(Integer)
                    at org.external.errorhandling.EmitGeneratorUnusedItemsTest.methodUnderTest(EmitGeneratorUnusedItemsTest.java:31)
                    Remaining items: [-2, -3]

                To resolve this error:

                 -> Modify object creation to ensure all items are consumed.
                    For example, at least 7 elements are requied to consume the status values:

                    List<Order> orders = Instancio.ofList(Order.class)
                        .size(7) // 5 shipped + 2 cancelled
                        .generate(field(Order::getStatus), gen -> gen.emit()
                               .items(OrderStatus.SHIPPED, 5)
                               .items(OrderStatus.CANCELLED, 2))
                        .create();

                 -> When using a composite selector such as 'allInts()'
                    each member gets its own copy of the items, for example:

                        generate(allInts(), gen -> gen.emit().items(1, 2, 3))

                    is equivalent to:

                        generate(all(int.class), gen -> gen.emit().items(1, 2, 3))
                        generate(all(Integer.class), gen -> gen.emit().items(1, 2, 3))

                    with the possibility of one of the items remaining unused.

                 -> Suppress this error with 'ignoreUnused()' to ignore remaining items:

                        gen.emit().items(1, 2, 3, 4, 5, 6, 7).ignoreUnused()

                """;
    }
}
