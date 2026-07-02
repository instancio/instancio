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
package org.instancio.internal.generation;

import org.instancio.generator.Generator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;

import java.util.Optional;

final class ElementOfGeneratorNodeHandler implements NodeHandler {

    private final ModelContext modelContext;
    private final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor;

    private ElementOfGeneratorNodeHandler(
            final ModelContext modelContext,
            final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor) {

        this.modelContext = modelContext;
        this.userSuppliedGeneratorProcessor = userSuppliedGeneratorProcessor;
    }

    static NodeHandler create(
            final ModelContext modelContext,
            final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor) {

        return modelContext.hasElementOfSelectors()
                ? new ElementOfGeneratorNodeHandler(modelContext, userSuppliedGeneratorProcessor)
                : NOOP_HANDLER;
    }

    @Override
    public GeneratorResult getResult(final InternalNode node) {
        final Optional<Generator<?>> generatorOpt = modelContext.getActiveElementOfGenerator(node);

        //noinspection OptionalIsPresent
        if (generatorOpt.isEmpty()) {
            return GeneratorResult.unresolvedResult();
        }

        return userSuppliedGeneratorProcessor.getGeneratorResult(node, generatorOpt.get());
    }
}
