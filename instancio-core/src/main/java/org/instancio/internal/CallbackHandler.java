/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.internal;

import org.instancio.OnCompleteCallback;
import org.instancio.generator.GeneratorResult;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class CallbackHandler {
    private static final Logger LOG = LoggerFactory.getLogger(CallbackHandler.class);

    private final ModelContext<?> context;
    private final Map<Node, List<GeneratorResult>> resultsForCallbacks = new IdentityHashMap<>();

    public CallbackHandler(final ModelContext<?> context) {
        this.context = context;
    }

    public void addResult(final Node node, final GeneratorResult result) {
        if (!getCallbacks(node).isEmpty()) {
            resultsForCallbacks.computeIfAbsent(node, res -> new ArrayList<>()).add(result);
        }
    }

    public void invokeCallbacks() {
        LOG.trace("Preparing to call {} callback(s)", resultsForCallbacks.size());
        resultsForCallbacks.forEach((node, results) -> {
            final List<OnCompleteCallback<?>> callbacks = getCallbacks(node);

            for (OnCompleteCallback<?> callback : callbacks) {
                LOG.trace("Invoking a callback for {} value(s) of {}", results.size(), node);
                for (GeneratorResult result : results) {
                    //noinspection unchecked
                    ((OnCompleteCallback) callback).onComplete(result.getValue());
                }
            }
        });
    }

    private List<OnCompleteCallback<?>> getCallbacks(final Node node) {
        return context.getUserSuppliedCallbacks(node);
    }
}
