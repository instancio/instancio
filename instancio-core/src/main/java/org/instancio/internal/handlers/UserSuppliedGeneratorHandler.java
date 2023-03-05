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
import org.instancio.internal.ApiValidator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.InternalGeneratorHint;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.generator.misc.InstantiatingGenerator;
import org.instancio.internal.instantiation.Instantiator;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

import static org.instancio.internal.util.ObjectUtils.defaultIfNull;

public class UserSuppliedGeneratorHandler implements NodeHandler {

    private final ModelContext<?> modelContext;
    private final GeneratorResolver generatorResolver;
    private final Instantiator instantiator;

    public UserSuppliedGeneratorHandler(final ModelContext<?> modelContext,
                                        final GeneratorResolver generatorResolver,
                                        final Instantiator instantiator) {
        this.modelContext = modelContext;
        this.generatorResolver = generatorResolver;
        this.instantiator = instantiator;
    }

    /**
     * If the context has enough information to generate a value for the field, then do so.
     * If not, return an empty {@link Optional} and proceed with the main generation flow.
     */
    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        return getUserSuppliedGenerator(node).map(generator -> {
            final Hints hints = generator.hints();
            final InternalGeneratorHint internalHint = hints.get(InternalGeneratorHint.class);
            final boolean nullable = internalHint != null && internalHint.nullableResult();

            if (modelContext.getRandom().diceRoll(nullable)) {
                return GeneratorResult.nullResult();
            }

            final Object value = generator.generate(modelContext.getRandom());
            return GeneratorResult.create(value, hints);
        }).orElse(GeneratorResult.emptyResult());
    }

    @SuppressWarnings("PMD.AvoidDeeplyNestedIfStmts")
    private Optional<Generator<?>> getUserSuppliedGenerator(final InternalNode node) {
        final Optional<Generator<?>> generatorOpt = modelContext.getGenerator(node);

        if (generatorOpt.isPresent()) {
            final Generator<?> generator = generatorOpt.get();
            ApiValidator.validateGeneratorUsage(node, generator);

            final Hints hints = generator.hints();
            final InternalGeneratorHint internalHint = hints.get(InternalGeneratorHint.class);

            if (internalHint != null && internalHint.isDelegating()) {
                final Class<?> forClass = defaultIfNull(internalHint.targetClass(), node.getTargetClass());
                final Generator<?> delegate = generatorResolver.get(node)
                        .orElse(new InstantiatingGenerator(instantiator, forClass));

                if (delegate instanceof AbstractGenerator<?>) {
                    final boolean nullable = ((AbstractGenerator<?>) generator).isNullable();
                    ((AbstractGenerator<?>) delegate).nullable(nullable);
                }
                return Optional.of(new GeneratorDecorator(delegate, hints));
            }
        }
        return generatorOpt;
    }
}
