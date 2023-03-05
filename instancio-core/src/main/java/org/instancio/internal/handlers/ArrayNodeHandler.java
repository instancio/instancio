/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.handlers;

import org.instancio.generator.Generator;
import org.instancio.internal.GeneratorSpecProcessor;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

public class ArrayNodeHandler implements NodeHandler {

    private final GeneratorResolver generatorResolver;
    private final ModelContext<?> context;
    private final GeneratorSpecProcessor beanValidationProcessors;

    public ArrayNodeHandler(
            final ModelContext<?> context,
            final GeneratorResolver generatorResolver,
            final GeneratorSpecProcessor beanValidationProcessor) {

        this.context = context;
        this.generatorResolver = generatorResolver;
        this.beanValidationProcessors = beanValidationProcessor;
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        if (node.getTargetClass().isArray()) {
            final Generator<?> generator = generatorResolver.get(node).orElseThrow(
                    () -> new IllegalStateException("Unable to get array generator for node: " + node));

            beanValidationProcessors.process(generator, node.getTargetClass(), node.getField());
            final Object arrayObject = generator.generate(context.getRandom());
            return GeneratorResult.create(arrayObject, generator.hints());
        }
        return GeneratorResult.emptyResult();
    }


}
