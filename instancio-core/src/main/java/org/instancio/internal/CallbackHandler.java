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
package org.instancio.internal;

import org.instancio.OnCompleteCallback;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.GenerationListener;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

class CallbackHandler implements GenerationListener {
    private static final Logger LOG = LoggerFactory.getLogger(CallbackHandler.class);

    private final ModelContext<?> context;
    private final Map<InternalNode, List<Object>> resultsForCallbacks = new IdentityHashMap<>();

    CallbackHandler(final ModelContext<?> context) {
        this.context = context;
    }

    @Override
    public void objectCreated(final InternalNode node, final GeneratorResult result) {
        // callbacks are not invoked on null values
        if (result.getValue() == null) {
            return;
        }
        final InternalGeneratorHint hint = result.getHints().get(InternalGeneratorHint.class);
        if (hint != null && hint.excludeFromCallbacks()) {
            return;
        }

        final Object instance = result.getValue();
        if (!getCallbacks(node).isEmpty()) {
            resultsForCallbacks.computeIfAbsent(node, res -> new ArrayList<>()).add(instance);
        }
    }

    void invokeCallbacks() {
        LOG.trace("Preparing to call {} callback(s)", resultsForCallbacks.size());
        resultsForCallbacks.forEach((node, results) -> {
            final List<OnCompleteCallback<?>> callbacks = getCallbacks(node);

            for (OnCompleteCallback<?> callback : callbacks) {
                LOG.trace("{} results for callbacks generated for: {}", results.size(), node);
                for (Object result : results) {
                    invokeCallback(callback, result, node);
                }
            }
        });
    }

    private List<OnCompleteCallback<?>> getCallbacks(final InternalNode node) {
        return context.getCallbacks(node);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void invokeCallback(final OnCompleteCallback<?> callback,
                                       final Object result,
                                       final InternalNode node) {
        try {
            ((OnCompleteCallback) callback).onComplete(result);
        } catch (ClassCastException ex) {
            final String errorMsg = String.format(
                    "onComplete() callback error.%n%n" +
                            "Node matched by the callback's selector:%n" +
                            " -> %s%n%n" +
                            "ClassCastException was thrown by the callback.%n" +
                            "This usually happens because the type declared by the callback%n" +
                            "does not match the actual type of the target object.%n%n" +
                            "Example:%n" +
                            "onComplete(all(Foo.class), (Bar wrongType) -> {%n" +
                            "               ^^^^^^^^^    ^^^^^^^^^^^^^%n" +
                            "})%n%n" +
                            "Caused by:%n%s", node, ex.getMessage());

            throw Fail.withUsageError(errorMsg, ex);
        } catch (Exception ex) {
            final String errorMsg = String.format(
                    "onComplete() callback error.%n%n" +
                            "Node matched by the callback's selector:%n" +
                            " -> %s%n%n" +
                            "Caused by:%n%s", node, ex.getMessage());

            throw Fail.withUsageError(errorMsg, ex);
        }
    }
}
