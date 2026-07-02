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
package org.instancio.internal;

import org.instancio.OnCompleteCallback;
import org.instancio.documentation.VisibleForTesting;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generation.GenerationListener;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Sonar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

class CallbackHandler implements GenerationListener {
    private static final Logger LOG = LoggerFactory.getLogger(CallbackHandler.class);

    private record CallbackEntry(
            InternalNode node,
            Object instance,
            List<OnCompleteCallback<?>> callbacks) {}

    private static final Comparator<CallbackEntry> CALLBACK_ENTRY_COMPARATOR = Comparator
            .comparingInt((CallbackEntry entry) -> entry.node.getDepth())
            .reversed();

    private final ModelContext context;
    private final List<CallbackEntry> callbackEntries = new ArrayList<>();

    private CallbackHandler(final ModelContext context) {
        this.context = context;
    }

    static CallbackHandler create(final ModelContext context) {
        return context.getSelectorMaps().hasCallbacks()
                ? new CallbackHandler(context)
                : new NoopCallbackHandler();
    }

    @Override
    public void objectCreated(final InternalNode node, final GeneratorResult result) {
        final Object value = result.getValue();
        if (value == null) { // callbacks are not invoked on null values
            return;
        }
        final InternalGeneratorHint hint = result.getHints().get(InternalGeneratorHint.class);
        if (hint != null && hint.excludeFromCallbacks()) {
            return;
        }

        // Resolve callbacks now, not at invocation time: elementOf() selectors match
        // against the element frame active while the object is being created.
        final List<OnCompleteCallback<?>> callbacks = context.getCallbacks(node);
        if (!callbacks.isEmpty()) {
            callbackEntries.add(new CallbackEntry(node, value, callbacks));
        }
    }

    void invokeCallbacks() {
        if (callbackEntries.isEmpty()) {
            return;
        }

        LOG.trace("Preparing to invoke callbacks for {} object(s)", callbackEntries.size());

        // Invoke callbacks bottom-up: a descendant always has greater depth than its ancestor,
        // so a stable sort by depth descending processes descendants first.
        callbackEntries.sort(CALLBACK_ENTRY_COMPARATOR);

        for (CallbackEntry entry : callbackEntries) {
            for (OnCompleteCallback<?> callback : entry.callbacks) {
                invokeCallback(callback, entry.instance, entry.node);
            }
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void invokeCallback(final OnCompleteCallback callback,
                                       final Object result,
                                       final InternalNode node) {
        try {
            callback.onComplete(result);
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

    //@formatter:off
    @SuppressWarnings({"NullAway", "DataFlowIssue", Sonar.ANNOTATE_PARAMETER_NULLABLE})
    @VisibleForTesting
    static final class NoopCallbackHandler extends CallbackHandler {
        NoopCallbackHandler() { super(null); }
        @Override public void objectCreated(final InternalNode node, final GeneratorResult result) { /* no-op */ }
        @Override void invokeCallbacks() { /* no-op */ }
    }
    //@formatter:on
}
