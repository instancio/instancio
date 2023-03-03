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

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.GeneratorSpecProcessor;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.util.CollectionGenerator;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CollectionNodeHandler implements NodeHandler {

    private final ModelContext<?> context;
    private final GeneratorSpecProcessor beanValidationProcessors;

    public CollectionNodeHandler(
            final ModelContext<?> context,
            final GeneratorSpecProcessor beanValidationProcessors) {

        this.context = context;
        this.beanValidationProcessors = beanValidationProcessors;
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        if (Collection.class.isAssignableFrom(node.getTargetClass())) {
            final CollectionGenerator<?> generator = new CollectionGenerator<>(
                    new GeneratorContext(context.getSettings(), context.getRandom()));

            generator.subtype(node.getTargetClass());
            beanValidationProcessors.process(generator, node.getTargetClass(), node.getField());
            return GeneratorResult.create(generator.generate(context.getRandom()), generator.hints());
        }
        return GeneratorResult.emptyResult();
    }
}
