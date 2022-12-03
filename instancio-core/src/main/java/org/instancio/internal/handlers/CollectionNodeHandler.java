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

import org.instancio.Random;
import org.instancio.generator.DataStructureHint;
import org.instancio.generator.Hints;
import org.instancio.generator.PopulateAction;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.util.EnumSetGenerator;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.util.Collection;
import java.util.EnumSet;
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
            if (EnumSet.class.isAssignableFrom(node.getTargetClass())) {
                return generateEnumSet(node);
            }

            final Hints hints = Hints.builder()
                    .hint(DataStructureHint.builder()
                            .dataStructureSize(randomSize())
                            .build())
                    .populateAction(PopulateAction.ALL)
                    .build();

            final Object collection = instantiator.instantiate(node.getTargetClass());
            final GeneratorResult result = GeneratorResult.create(collection, hints);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @SuppressWarnings({"unchecked", Sonar.RAW_USE_OF_PARAMETERIZED_CLASS})
    private Optional<GeneratorResult> generateEnumSet(final Node collectionNode) {
        final Class<Enum> enumClass = (Class<Enum>) collectionNode.getOnlyChild().getTargetClass();
        // TODO refactor
        final Enum<?>[] enumValues = ReflectionUtils.getEnumValues(enumClass);
        final int enumSetSize = Math.min(randomSize(), enumValues.length);
        final Hints hints = Hints.builder()
                .populateAction(PopulateAction.ALL)
                .hint(DataStructureHint.builder()
                        .dataStructureSize(enumSetSize)
                        .build())
                .build();
        final EnumSetGenerator<?> generator = new EnumSetGenerator<>(enumClass);
        return Optional.of(GeneratorResult.create(generator.generate(context.getRandom()), hints));
    }

    private int randomSize() {
        final Random random = context.getRandom();
        final Settings settings = context.getSettings();
        return random.intRange(settings.get(Keys.COLLECTION_MIN_SIZE), settings.get(Keys.COLLECTION_MAX_SIZE));
    }
}
