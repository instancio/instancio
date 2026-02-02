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
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

final class UserSuppliedGeneratorHandler implements NodeHandler {

    private final ModelContext modelContext;
    private final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor;

    private UserSuppliedGeneratorHandler(
            final ModelContext modelContext,
            final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor) {

        this.modelContext = modelContext;
        this.userSuppliedGeneratorProcessor = userSuppliedGeneratorProcessor;
    }

    static NodeHandler create(final ModelContext modelContext,
                              final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor) {
        return modelContext.getSelectorMaps().hasGenerators()
                ? new UserSuppliedGeneratorHandler(modelContext, userSuppliedGeneratorProcessor)
                : NOOP_HANDLER;
    }

    /**
     * If the context has enough information to generate a value for the field, then do so.
     * If not, return an empty {@link Optional} and proceed with the main generation flow.
     */
    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final Optional<Generator<?>> generatorOpt = modelContext.getGenerator(node);

        //noinspection OptionalIsPresent
        if (generatorOpt.isEmpty()) {
            return GeneratorResult.emptyResult();
        }

        return userSuppliedGeneratorProcessor.getGeneratorResult(node, generatorOpt.get());
    }
}
