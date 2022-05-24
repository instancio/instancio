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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * Entry point for generating an object.
 * <p>
 * A new instance of this class should be created for each object generated via {@link #createRootObject()}.
 */
class InstancioEngine {
    private static final Logger LOG = LoggerFactory.getLogger(InstancioEngine.class);

    private final GeneratorFacade generatorFacade;
    private final Queue<CreateItem> queue = new ArrayDeque<>();
    private final ModelContext<?> context;
    private final Node rootNode;
    private final CallbackHandler callbackHandler;
    private final List<GenerationListener> listeners = new ArrayList<>();

    InstancioEngine(InternalModel<?> model) {
        context = model.getModelContext();
        rootNode = model.getRootNode();
        callbackHandler = new CallbackHandler(context);
        generatorFacade = new GeneratorFacade(context);
        listeners.add(callbackHandler);
        listeners.add(new GeneratedNullValueListener(context));
    }

    void notifyListeners(final Node node, @Nullable final Object generatedObject) {
        listeners.forEach(it -> it.objectCreated(node, generatedObject));
    }

    @SuppressWarnings("unchecked")
    <T> T createRootObject() {
        final Optional<GeneratorResult> optResult = createObject(rootNode, /* owner = */ null);
        final T rootResult = (T) optResult.map(GeneratorResult::getValue).orElse(null);
        callbackHandler.invokeCallbacks();
        context.reportUnusedSelectorWarnings();
        return rootResult;
    }

    Optional<GeneratorResult> createObject(final Node node, @Nullable final Object owner) {
        final Optional<GeneratorResult> optResult = generateValue(node, owner);
        if (!optResult.isPresent()) {
            return Optional.empty();
        }

        final GeneratorResult rootResult = optResult.get();
        node.accept(new PopulatingNodeVisitor(owner, rootResult, context, queue, this));

        while (!queue.isEmpty()) {
            process(queue.poll());
        }

        return optResult;
    }

    private void process(final CreateItem createItem) {
        LOG.trace("Creating: {}", createItem);

        final Node node = createItem.getNode();
        final Optional<GeneratorResult> result = generateValue(node, createItem.getOwner());
        result.ifPresent(generatorResult -> node.accept(
                new PopulatingNodeVisitor(createItem.getOwner(), generatorResult, context, queue, this)));
    }

    private Optional<GeneratorResult> generateValue(final Node node, @Nullable final Object owner) {
        final Optional<GeneratorResult> generatorResult = generatorFacade.generateNodeValue(node, owner);
        notifyListeners(node, generatorResult.map(GeneratorResult::getValue).orElse(null));
        return generatorResult;
    }
}
