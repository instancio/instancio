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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CallbackHandler {

    private final ModelContext<?> context;
    private final Map<Node, List<GeneratorResult>> callbackItems = new LinkedHashMap<>();

    public CallbackHandler(final ModelContext<?> context) {
        this.context = context;
    }

    public void addResult(final Node node, final GeneratorResult result) {
        final OnCompleteCallback<?> callback = getCallbackHandler(node);
        if (callback != null) {
            callbackItems.computeIfAbsent(node, res -> new ArrayList<>()).add(result);
        }
    }

    public void invokeCallbacks() {
        callbackItems.forEach((node, results) -> {
            final OnCompleteCallback<Object> callback = getCallbackHandler(node);
            results.forEach(result -> callback.onComplete(result.getValue()));
        });
    }

    @SuppressWarnings("unchecked")
    private <T> OnCompleteCallback<T> getCallbackHandler(final Node node) {
        return (OnCompleteCallback<T>) context.getUserSuppliedCallback(node);
    }
}
