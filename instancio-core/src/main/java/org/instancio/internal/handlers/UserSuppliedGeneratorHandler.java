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

import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorHint;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.generator.misc.InstantiatingGenerator;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;

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
    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        return getUserSuppliedGenerator(node).map(g -> {
            ApiValidator.validateGeneratorUsage(node, g);
            return GeneratorResult.create(g.generate(modelContext.getRandom()), g.hints());
        });
    }

    private Optional<Generator<?>> getUserSuppliedGenerator(final Node node) {
        final Optional<Generator<?>> generatorOpt = modelContext.getGenerator(node);

        if (generatorOpt.isPresent()) {
            final Generator<?> generator = generatorOpt.get();
            final Hints hints = generator.hints();
            final GeneratorHint generatorHint = hints.get(GeneratorHint.class);

            if (generatorHint != null && generatorHint.isDelegating()) {
                final Class<?> forClass = defaultIfNull(generatorHint.targetClass(), node.getTargetClass());
                final Generator<?> generatingDelegate = generatorResolver
                        .get(forClass)
                        .orElseGet(() -> new InstantiatingGenerator(instantiator, forClass));

                return Optional.of(new GeneratorDecorator(generatingDelegate, hints));
            }
        }
        return generatorOpt;
    }
}
