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
package org.other.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR})
@ExtendWith(InstancioExtension.class)
class EmitGeneratorUnusedItemsTest {

    @Test
    void unusedItemsError() {
        final InstancioApi<List<Integer>> api = Instancio.ofList(Integer.class)
                .size(1)
                .generate(all(Integer.class), gen -> gen.emit().items(-1, -2, -3));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage(String.format("%n" +
                        "This error was thrown because not all the items provided via emit() have been consumed.%n" +
                        "The following items are still remaining:%n" +
                        "%n" +
                        " -> all(Integer)%n" +
                        "    at org.other.test.features.mode.EmitGeneratorUnusedItemsTest.unusedItemsError(EmitGeneratorUnusedItemsTest.java:40)%n" +
                        "    Remaining items: [-2, -3]%n" +
                        "%n" +
                        "To resolve this error:%n" +
                        "%n" +
                        " -> Modify object creation to ensure all items are consumed.%n" +
                        "    For example, at least 7 elements are requied to consume the status values:%n" +
                        "%n" +
                        "    List<Order> orders = Instancio.ofList(Order.class)%n" +
                        "        .size(7) // 5 shipped + 2 cancelled%n" +
                        "        .generate(field(Order::getStatus), gen -> gen.emit()%n" +
                        "               .items(OrderStatus.SHIPPED, 5)%n" +
                        "               .items(OrderStatus.CANCELLED, 2))%n" +
                        "        .create();%n" +
                        "%n" +
                        " -> When using a composite selector such as 'allInts()'%n" +
                        "    each member gets its own copy of the items, for example:%n" +
                        "%n" +
                        "        generate(allInts(), gen -> gen.emit().items(1, 2, 3))%n" +
                        "%n" +
                        "    is equivalent to:%n" +
                        "%n" +
                        "        generate(all(int.class), gen -> gen.emit().items(1, 2, 3))%n" +
                        "        generate(all(Integer.class), gen -> gen.emit().items(1, 2, 3))%n" +
                        "%n" +
                        "    with the possibility of one of the items remaining unused.%n" +
                        "%n" +
                        " -> Suppress this error with 'ignoreUnused()' to ignore remaining items:%n" +
                        "%n" +
                        "        gen.emit().items(1, 2, 3, 4, 5, 6, 7).ignoreUnused()%n"));
    }

}
