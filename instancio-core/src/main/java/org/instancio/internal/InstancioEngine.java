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

import org.instancio.generator.GeneratorResult;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

class InstancioEngine {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioEngine.class);

    private final GeneratorFacade generatorFacade;
    private final Queue<CreateItem> queue = new ArrayDeque<>();
    private final ModelContext<?> context;
    private final Node rootNode;
    private final CallbackHandler callbackHandler;

    InstancioEngine(InternalModel<?> model) {
        this.context = model.getModelContext();
        this.rootNode = model.getRootNode();
        this.callbackHandler = new CallbackHandler(context);
        this.generatorFacade = new GeneratorFacade(context, callbackHandler);
    }

    @SuppressWarnings("unchecked")
    <T> T createRootObject() {
        final Optional<GeneratorResult> optResult = createObject(rootNode, /* owner = */ null);
        final T rootResult = (T) optResult.map(GeneratorResult::getValue).orElse(null);
        callbackHandler.invokeCallbacks();
        return rootResult;
    }

    Optional<GeneratorResult> createObject(final Node node, @Nullable final Object owner) {
        final Optional<GeneratorResult> optResult = generatorFacade.generateNodeValue(node, owner);
        if (!optResult.isPresent()) {
            return Optional.empty();
        }

        final GeneratorResult rootResult = optResult.get();
        node.accept(new PopulatingNodeVisitor(owner, rootResult, context, queue, this));

        while (!queue.isEmpty()) {
            processNextItem(queue.poll());
        }

        callbackHandler.addResult(node, rootResult);
        return optResult;
    }

    private void processNextItem(final CreateItem createItem) {
        LOG.trace("Creating: {}", createItem);

        final Node node = createItem.getNode();
        final Optional<GeneratorResult> result = generatorFacade.generateNodeValue(node, createItem.getOwner());
        if (result.isPresent()) {
            node.accept(new PopulatingNodeVisitor(createItem.getOwner(), result.get(), context, queue, this));
            callbackHandler.addResult(node, result.get());
        }
    }
}
