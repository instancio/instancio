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
package org.instancio.internal.handlers;

import org.instancio.internal.GeneratedHints;
import org.instancio.internal.GeneratorResult;
import org.instancio.internal.nodes.CollectionNode;
import org.instancio.internal.ModelContext;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.random.RandomProvider;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.settings.Setting;
import org.instancio.settings.Settings;
import org.instancio.util.Verify;

import java.util.Collection;
import java.util.Optional;

public class CollectionNodeHandler implements NodeHandler {

    private final ModelContext<?> context;
    private final Instantiator instantiator;

    public CollectionNodeHandler(final ModelContext<?> context, final Instantiator instantiator) {
        this.context = context;
        this.instantiator = instantiator;
    }

    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        if (node instanceof CollectionNode) {
            Verify.isTrue(Collection.class.isAssignableFrom(node.getKlass()), "Expected a collection type: %s", node.getKlass());
            final Class<?> effectiveType = context.getSubtypeMapping(node.getKlass());
            final GeneratedHints hints = GeneratedHints.builder().dataStructureSize(randomSize()).build();
            final GeneratorResult result = GeneratorResult.create(instantiator.instantiate(effectiveType), hints);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private int randomSize() {
        final RandomProvider random = context.getRandomProvider();
        final Settings settings = context.getSettings();
        return random.intBetween(settings.get(Setting.COLLECTION_MIN_SIZE), settings.get(Setting.COLLECTION_MAX_SIZE));
    }
}
