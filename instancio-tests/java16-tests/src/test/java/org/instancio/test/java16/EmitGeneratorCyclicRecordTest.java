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
package org.instancio.test.java16;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR, Feature.CYCLIC})
@ExtendWith(InstancioExtension.class)
class EmitGeneratorCyclicRecordTest {

    private record OrderRecord(List<OrderItemRecord> items, OrderStatus status) {}

    private record OrderItemRecord(OrderRecord order) {}

    private enum OrderStatus {SHIPPED, DELIVERED, SUBMITTED}

    @Test
    void emitWithCyclicClasses() {
        final OrderStatus[] statuses = {
                OrderStatus.SUBMITTED, OrderStatus.SUBMITTED, OrderStatus.SUBMITTED,
                OrderStatus.SHIPPED, OrderStatus.SHIPPED, OrderStatus.SHIPPED,
                OrderStatus.DELIVERED, OrderStatus.DELIVERED, OrderStatus.DELIVERED
        };

        final List<OrderRecord> result = Instancio.ofList(OrderRecord.class)
                .size(statuses.length)
                .generate(field(OrderRecord::status), gen -> gen.emit().items(statuses))
                .create();

        assertThat(result).extracting(OrderRecord::status).containsExactly(statuses);
    }

}
