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
package org.instancio.internal.generation;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.nodes.NodeKind;
import org.instancio.internal.util.Constants;
import org.instancio.internal.util.Verify;
import org.instancio.settings.Keys;

import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Store for POJOs that is used to resolve back-references when
 * {@link Keys#SET_BACK_REFERENCES} is enabled.
 */
class GeneratedPojoStore {

    private final Map<InternalNode, Object> generatedPojos = new IdentityHashMap<>();

    private GeneratedPojoStore() {
        // created via static factory method
    }

    static GeneratedPojoStore createStore(final ModelContext<?> context) {
        final boolean backRefsEnabled = context.getSettings().get(Keys.SET_BACK_REFERENCES);

        return backRefsEnabled
                ? new GeneratedPojoStore()
                : new NoopGeneratedPojoStore();
    }

    GeneratorResult getParentObject(final InternalNode node) {
        Verify.isTrue(node.isCyclic(), "Non-cyclic node: %s", node);

        final Class<?> targetClass = node.getTargetClass();

        for (InternalNode n = node.getParent(); n != null; n = n.getParent()) {
            if (n.getTargetClass() != targetClass) {
                continue;
            }
            final Object o = generatedPojos.get(n);
            if (o != null) {
                return GeneratorResult.create(o, Constants.DO_NOT_MODIFY_HINT);
            }
        }
        return GeneratorResult.emptyResult();
    }

    void putValue(final InternalNode node, final GeneratorResult result) {
        if (node.is(NodeKind.POJO) && !result.containsNull()) {
            generatedPojos.put(node, result.getValue());
        }
    }

    private static final class NoopGeneratedPojoStore extends GeneratedPojoStore {
        @Override
        GeneratorResult getParentObject(final InternalNode node) {
            return GeneratorResult.emptyResult();
        }

        @Override
        void putValue(final InternalNode node, final GeneratorResult result) {
            // no-op
        }
    }
}
