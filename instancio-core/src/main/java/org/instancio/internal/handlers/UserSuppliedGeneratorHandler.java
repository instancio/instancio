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
import org.instancio.generator.Hints;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.EmitGenerator;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class UserSuppliedGeneratorHandler implements NodeHandler {

    private final ModelContext<?> modelContext;
    private final UserSuppliedGeneratorProcessor userSuppliedGeneratorProcessor;

    public UserSuppliedGeneratorHandler(final ModelContext<?> modelContext,
                                        final GeneratorResolver generatorResolver,
                                        final Instantiator instantiator) {
        this.modelContext = modelContext;
        this.userSuppliedGeneratorProcessor = new UserSuppliedGeneratorProcessor(
                generatorResolver, instantiator);
    }

    /**
     * If the context has enough information to generate a value for the field, then do so.
     * If not, return an empty {@link Optional} and proceed with the main generation flow.
     */
    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final Optional<Generator<?>> generatorOpt = modelContext.getGenerator(node);

        if (!generatorOpt.isPresent()) {
            return GeneratorResult.emptyResult();
        }

        final Generator<?> generator = userSuppliedGeneratorProcessor.processGenerator(
                generatorOpt.get(), node);

        if (generator instanceof EmitGenerator) {
            final EmitGeneratorHelper helper = new EmitGeneratorHelper(modelContext);
            return helper.getResult((EmitGenerator<?>) generator, node);
        }

        final Hints hints = generator.hints();
        final InternalGeneratorHint internalHint = hints.get(InternalGeneratorHint.class);
        final boolean nullable = internalHint != null && internalHint.nullableResult();

        if (modelContext.getRandom().diceRoll(nullable)) {
            return GeneratorResult.nullResult();
        }

        final Object value = generator.generate(modelContext.getRandom());
        return GeneratorResult.create(value, hints);
    }
}
