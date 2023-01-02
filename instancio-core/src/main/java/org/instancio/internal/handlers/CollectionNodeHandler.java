/*
 *  Copyright 2022-2023 the original author or authors.
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

import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.util.Collection;
import java.util.Optional;

public class CollectionNodeHandler implements NodeHandler {

    private final ModelContext<?> context;
    private final Instantiator instantiator;

    public CollectionNodeHandler(final ModelContext<?> context,
                                 final Instantiator instantiator) {
        this.context = context;
        this.instantiator = instantiator;
    }

    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        if (Collection.class.isAssignableFrom(node.getTargetClass())) {
            final Hints hints = Hints.builder()
                    .with(CollectionHint.builder()
                            .generateElements(randomSize())
                            .build())
                    .afterGenerate(AfterGenerate.POPULATE_ALL)
                    .build();

            final Object collection = instantiator.instantiate(node.getTargetClass());
            final GeneratorResult result = GeneratorResult.create(collection, hints);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private int randomSize() {
        final Random random = context.getRandom();
        final Settings settings = context.getSettings();
        return random.intRange(settings.get(Keys.COLLECTION_MIN_SIZE), settings.get(Keys.COLLECTION_MAX_SIZE));
    }
}
