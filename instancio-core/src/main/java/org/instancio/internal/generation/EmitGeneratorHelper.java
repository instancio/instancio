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
package org.instancio.internal.generation;

import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.generator.misc.EmitGenerator.WhenEmptyAction;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.instancio.internal.util.Constants.NL;

class EmitGeneratorHelper {

    private static final Hints EMIT_NULL_HINT = Hints.builder()
            .with(InternalGeneratorHint.builder().emitNull(true).build())
            .build();

    private static final GeneratorResult NULL_RESULT = GeneratorResult.create(null, EMIT_NULL_HINT);

    private final ModelContext modelContext;

    // Keep track of values emitted by a generator and nodes the values
    // were assigned to for error reporting
    private final Map<Generator<?>, Map<InternalNode, List<Object>>> generatorEmittedValues = new HashMap<>();

    EmitGeneratorHelper(final ModelContext modelContext) {
        this.modelContext = modelContext;
    }

    GeneratorResult getResult(final EmitGenerator<?> generator, final InternalNode node) {
        if (generator.hasMore() || generator.getWhenEmptyAction() == WhenEmptyAction.RECYCLE) {
            final Object result = generator.generate(modelContext.getRandom());

            final Map<InternalNode, List<Object>> emittedValues =
                    generatorEmittedValues.computeIfAbsent(generator, k -> new LinkedHashMap<>());

            generatorEmittedValues.put(generator, emittedValues);

            final List<Object> values = emittedValues.computeIfAbsent(node, k -> new ArrayList<>());
            values.add(result);

            return result == null
                    ? NULL_RESULT
                    : GeneratorResult.create(result, generator.hints());
        }

        final WhenEmptyAction whenEmptyAction = generator.getWhenEmptyAction();

        if (whenEmptyAction == WhenEmptyAction.EMIT_NULL) {
            return NULL_RESULT;
        } else if (whenEmptyAction == WhenEmptyAction.EMIT_RANDOM) {
            return GeneratorResult.emptyResult();
        }

        final Map<InternalNode, List<Object>> emittedValues =
                generatorEmittedValues.getOrDefault(generator, Collections.emptyMap());

        throw Fail.withUsageError(createdErrorMsg(node, emittedValues));
    }

    private static String createdErrorMsg(final InternalNode node, final Map<InternalNode, List<Object>> emittedValues) {
        final StringBuilder sb = new StringBuilder(1500);
        sb.append("no item is available to emit() for node:").append(NL)
                .append(NL)
                .append(Format.nodePathToRootBlock(node)).append(NL)
                .append(NL);

        if (emittedValues.isEmpty()) {
            sb.append("Please specify one or more values to emit()").append(NL)
                    .append(NL)
                    .append("    Example:").append(NL)
                    .append("    List<Order> orders = Instancio.ofList(Order.class)").append(NL)
                    .append("        .size(7)").append(NL)
                    .append("        .generate(field(Order::getStatus), gen -> gen.emit()").append(NL)
                    .append("                .items(OrderStatus.RECEIVED, OrderStatus.SHIPPED)").append(NL)
                    .append("                .item(OrderStatus.COMPLETED, 3)").append(NL)
                    .append("                .item(OrderStatus.CANCELLED, 2))").append(NL)
                    .append("        .create();");
        } else {
            sb.append("Previously emitted values:").append(NL)
                    .append(NL);

            emittedValues.forEach((assignedNode, nodeValues) -> {
                sb.append(" -> Node:   ").append(Format.formatAsTreeNode(assignedNode)).append(NL)
                        .append("    Values: ").append(nodeValues).append(NL)
                        .append(NL);
            });

            sb.append("Another value is required for:").append(NL)
                    .append(NL)
                    .append(" -> Node:   ").append(Format.formatAsTreeNode(node)).append(NL)
                    .append(NL)
                    .append("But there are no values left to emit.").append(NL)
                    .append("Throwing exception because 'whenEmptyThrowException()' is enabled.");
        }
        return sb.toString();
    }
}
