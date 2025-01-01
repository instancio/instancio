/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.generator.Generator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.SpiGeneratorResolver;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

class SpiGeneratorNodeHandler implements NodeHandler {

    private final ModelContext modelContext;
    private final SpiGeneratorResolver spiGeneratorResolver;

    SpiGeneratorNodeHandler(
            final ModelContext modelContext,
            final SpiGeneratorResolver spiGeneratorResolver) {

        this.modelContext = modelContext;
        this.spiGeneratorResolver = spiGeneratorResolver;
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final Generator<?> generator = spiGeneratorResolver.getSpiGenerator(node);

        if (generator == null) {
            return GeneratorResult.emptyResult();
        }

        final Object value = generator.generate(modelContext.getRandom());
        return GeneratorResult.create(value, generator.hints());
    }
}
