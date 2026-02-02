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
package org.instancio.test.features.generator.misc;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.cyclic.onetomany.DetailPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({
        Feature.GENERATOR,
        Feature.EMIT_GENERATOR,
        Feature.CYCLIC,
        Feature.SELECTOR,
        Feature.DEPTH_SELECTOR,
        Feature.PREDICATE_SELECTOR
})
@ExtendWith(InstancioExtension.class)
class EmitGeneratorCyclicPojoTest {

    @Test
    void emitCollectionElementField() {
        final MainPojo result = Instancio.of(MainPojo.class)
                .generate(all(List.class), gen -> gen.collection().size(3))
                .generate(field(DetailPojo::getId), gen -> gen.emit().items(1L, 2L, 3L))
                .create();

        assertThat(result.getDetailPojos())
                .extracting(DetailPojo::getId)
                .containsExactly(1L, 2L, 3L);
    }

    @MethodSource("statusSelectors")
    @ParameterizedTest
    void listOfOrdersWithExpectedStatuses(final TargetSelector selector) {
        final OrderStatus[] statuses = {
                OrderStatus.SUBMITTED, OrderStatus.SUBMITTED,
                OrderStatus.SHIPPED, OrderStatus.SHIPPED,
                OrderStatus.DELIVERED, OrderStatus.DELIVERED
        };

        final List<Order> result = Instancio.ofList(Order.class)
                .size(statuses.length)
                .generate(selector, gen -> gen.emit().items(statuses))
                .create();

        assertThat(result)
                .extracting(o -> o.status)
                .containsExactly(statuses);
    }

    private static Stream<Arguments> statusSelectors() {
        final int depth = 2;
        final Predicate<Integer> predicate = d -> d == depth;

        return Stream.of(
                // regular and predicate selectors
                Arguments.of(all(OrderStatus.class)),
                Arguments.of(all(OrderStatus.class).atDepth(depth)),
                Arguments.of(field(Order::getStatus)),
                Arguments.of(field(Order::getStatus).atDepth(depth)),
                Arguments.of(field(Order.class, "status")),
                Arguments.of(field(Order.class, "status").atDepth(depth)),
                Arguments.of(fields(f -> f.getType() == OrderStatus.class)),
                Arguments.of(fields(f -> f.getType() == OrderStatus.class).atDepth(depth)),
                Arguments.of(types(t -> t == OrderStatus.class)),
                Arguments.of(types(t -> t == OrderStatus.class).atDepth(depth)),

                // predicate selectors
                Arguments.of(fields(f -> f.getType() == OrderStatus.class)),
                Arguments.of(fields(f -> f.getType() == OrderStatus.class).atDepth(predicate)),
                Arguments.of(types(t -> t == OrderStatus.class)),
                Arguments.of(types(t -> t == OrderStatus.class).atDepth(predicate)),

                // predicate selector builders
                Arguments.of(fields().ofType(OrderStatus.class)),
                Arguments.of(fields().ofType(OrderStatus.class).atDepth(predicate)),
                Arguments.of(types().of(OrderStatus.class)),
                Arguments.of(types().of(OrderStatus.class).atDepth(predicate))
        );
    }

    // used via reflection
    @SuppressWarnings("unused")
    private static class Order {
        private List<OrderItem> items;
        private OrderStatus status;

        OrderStatus getStatus() {
            return status;
        }
    }

    @SuppressWarnings("unused")
    private static class OrderItem {
        private Order order;
    }

    private enum OrderStatus {
        SHIPPED, DELIVERED, SUBMITTED
    }
}
