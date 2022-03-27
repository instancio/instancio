/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.internal;

import org.instancio.internal.model.InternalModel;
import org.instancio.internal.model.ModelContext;
import org.instancio.internal.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Queue;

import static java.util.stream.Collectors.toList;

class InstancioDriver {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioDriver.class);

    private final GeneratorFacade generatorFacade;
    private final Queue<CreateItem> queue = new ArrayDeque<>();
    private final ModelContext<?> context;
    private final Node rootNode;

    InstancioDriver(InternalModel<?> model) {
        this.context = model.getModelContext();
        this.rootNode = model.getRootNode();
        this.generatorFacade = new GeneratorFacade(context);
    }

    <T> T createEntryPoint() {
        final GeneratorResult rootResult = generatorFacade.generateNodeValue(rootNode, null);
        final Object value = rootResult.getValue();

        enqueueChildrenOf(rootNode, rootResult, queue);

        rootNode.accept(new PopulatingNodeVisitor(null, rootResult, generatorFacade, context, queue));

        while (!queue.isEmpty()) {
            processNextItem(queue.poll());
        }

        //noinspection unchecked
        return (T) value;
    }

    private void processNextItem(final CreateItem createItem) {
        LOG.trace("Creating: {}", createItem);

        final Node node = createItem.getNode();
        if (!isIgnored(node)) {
            final GeneratorResult generatorResult = generatorFacade.generateNodeValue(node, createItem.getOwner());
            node.accept(new PopulatingNodeVisitor(createItem.getOwner(), generatorResult, generatorFacade, context, queue));
        }
    }

    private boolean isIgnored(final Node node) {
        return node.getField() == null
                || context.isIgnored(node.getField())
                || context.isIgnored(node.getKlass())
                || Modifier.isStatic(node.getField().getModifiers());
    }

    private static void enqueueChildrenOf(Node node, GeneratorResult result, Queue<CreateItem> queue) {
        if (!result.ignoreChildren()) {
            final Object owner = result.getValue();
            queue.addAll(node.getChildren().stream()
                    .map(it -> new CreateItem(it, owner))
                    .collect(toList()));
        }
    }
}
