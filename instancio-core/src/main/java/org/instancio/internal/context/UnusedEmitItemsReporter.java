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
package org.instancio.internal.context;

import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.Generator;
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.selectors.UnusedSelectorDescription;

import java.util.Collection;

import static org.instancio.internal.util.Constants.NL;

class UnusedEmitItemsReporter {

    private final SelectorMap<Generator<?>> selectorMap;

    UnusedEmitItemsReporter(final SelectorMap<Generator<?>> selectorMap) {
        this.selectorMap = selectorMap;
    }

    void report() {
        selectorMap.forEach((selector, generator) -> {
            if (generator instanceof EmitGenerator) {
                final EmitGenerator<?> g = (EmitGenerator<?>) generator;
                if (!g.isIgnoreUnused() && g.hasMore()) {
                    throw new InstancioApiException(buildUnusedItemsErrorMessage(selector, g.getItems()));
                }
            }
        });
    }

    private static String buildUnusedItemsErrorMessage(
            final TargetSelector selector,
            final Collection<?> items) {

        //noinspection StringBufferReplaceableByString
        return new StringBuilder(256)
                .append(NL)
                .append("This error was thrown because not all the items provided via emit() have been consumed.").append(NL)
                .append("The following items are still remaining:").append(NL)
                .append(NL)
                .append(" -> ").append(((UnusedSelectorDescription) selector).getDescription()).append(NL)
                .append("    Remaining items: ").append(items).append(NL)
                .append(NL)
                .append("To resolve this error:").append(NL)
                .append(NL)
                .append(" -> Modify object creation to ensure all items are consumed.").append(NL)
                .append("    For example, at least 7 elements are requied to consume the status values:").append(NL)
                .append(NL)
                .append("    List<Order> orders = Instancio.ofList(Order.class)").append(NL)
                .append("        .size(7) // 5 shipped + 2 cancelled").append(NL)
                .append("        .generate(field(Order::getStatus), gen -> gen.emit()").append(NL)
                .append("               .items(OrderStatus.SHIPPED, 5)").append(NL)
                .append("               .items(OrderStatus.CANCELLED, 2))").append(NL)
                .append("        .create();").append(NL)
                .append(NL)
                .append(" -> When using a composite selector such as 'allInts()'").append(NL)
                .append("    each member gets its own copy of the items, for example:").append(NL)
                .append(NL)
                .append("        generate(allInts(), gen -> gen.emit().items(1, 2, 3))").append(NL)
                .append(NL)
                .append("    is equivalent to:").append(NL)
                .append(NL)
                .append("        generate(all(int.class), gen -> gen.emit().items(1, 2, 3))").append(NL)
                .append("        generate(all(Integer.class), gen -> gen.emit().items(1, 2, 3))").append(NL)
                .append(NL)
                .append("    with the possibility of one of the items remaining unused.").append(NL)
                .append(NL)
                .append(" -> Suppress this error with 'ignoreUnused()' to ignore remaining items:").append(NL)
                .append(NL)
                .append("        gen.emit().items(1, 2, 3, 4, 5, 6, 7).ignoreUnused()").append(NL)
                .toString();
    }
}
