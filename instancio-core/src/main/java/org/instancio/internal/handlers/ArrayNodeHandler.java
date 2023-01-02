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

import org.instancio.generator.Generator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.Node;

import java.util.Optional;

public class ArrayNodeHandler implements NodeHandler {

    private final GeneratorResolver generatorResolver;
    private final ModelContext<?> context;

    public ArrayNodeHandler(final ModelContext<?> context, final GeneratorResolver generatorResolver) {
        this.context = context;
        this.generatorResolver = generatorResolver;
    }

    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        if (node.getTargetClass().isArray()) {
            final Generator<?> generator = generatorResolver.get(node.getTargetClass()).orElseThrow(
                    () -> new IllegalStateException("Unable to get array generator for node: " + node));

            final Object arrayObject = generator.generate(context.getRandom());
            final GeneratorResult result = GeneratorResult.create(arrayObject, generator.hints());
            return Optional.of(result);
        }
        return Optional.empty();
    }


}
