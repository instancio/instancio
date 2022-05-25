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
import org.instancio.generator.GeneratedHints;
import org.instancio.generator.GeneratorResult;
import org.instancio.generator.util.EnumSetGenerator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.CollectionNode;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Sonar;
import org.instancio.util.Verify;

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

        if (node instanceof CollectionNode) {
            Verify.isTrue(Collection.class.isAssignableFrom(node.getTargetClass()),
                    "Expected a collection type: %s", node.getTargetClass());

            if (EnumSet.class.isAssignableFrom(node.getTargetClass())) {
                return generateEnumSet((CollectionNode) node);
            }

            final Class<?> effectiveType = context.getSubtypeMapping(node.getTargetClass());
            final GeneratedHints hints = GeneratedHints.builder().dataStructureSize(randomSize()).build();
            final GeneratorResult result = GeneratorResult.create(instantiator.instantiate(effectiveType), hints);
            return Optional.of(result);
        }
        return Optional.empty();
    }

    @SuppressWarnings({"unchecked", Sonar.RAW_USE_OF_PARAMETERIZED_CLASS})
    private Optional<GeneratorResult> generateEnumSet(final CollectionNode collectionNode) {
        final Class<Enum> enumClass = (Class<Enum>) collectionNode.getOnlyChild().getTargetClass();
        final Enum<?>[] enumValues = ReflectionUtils.getEnumValues(enumClass);
        final int enumSetSize = Math.min(randomSize(), enumValues.length);
        final GeneratedHints hints = GeneratedHints.builder().dataStructureSize(enumSetSize).build();
        final EnumSetGenerator<?> generator = new EnumSetGenerator<>(enumClass);
        return Optional.of(GeneratorResult.create(generator.generate(context.getRandom()), hints));
    }

    private int randomSize() {
        final Random random = context.getRandom();
        final Settings settings = context.getSettings();
        return random.intRange(settings.get(Keys.COLLECTION_MIN_SIZE), settings.get(Keys.COLLECTION_MAX_SIZE));
    }
}
