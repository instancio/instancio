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

import org.instancio.Generator;
import org.instancio.generators.InstantiatingGenerator;
import org.instancio.internal.GeneratorResolver;
import org.instancio.internal.GeneratorResult;
import org.instancio.internal.ModelContext;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.util.ObjectUtils;

import java.util.Optional;

public class UserSuppliedGeneratorHandler implements NodeHandler {

    private final ModelContext<?> context;
    private final GeneratorResolver generatorResolver;
    private final Instantiator instantiator;

    public UserSuppliedGeneratorHandler(final ModelContext<?> context,
                                        final GeneratorResolver generatorResolver,
                                        final Instantiator instantiator) {
        this.context = context;
        this.generatorResolver = generatorResolver;
        this.instantiator = instantiator;
    }

    /**
     * If the context has enough information to generate a value for the field, then do so.
     * If not, return an empty {@link Optional} and proceed with the main generation flow.
     */
    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        return getUserSuppliedGenerator(node).map(g -> GeneratorResult.create(g.generate(), g.getHints()));
    }

    private Optional<Generator<?>> getUserSuppliedGenerator(final Node node) {
        Optional<Generator<?>> generatorOpt = context.getUserSuppliedGenerator(node.getField());
        if (!generatorOpt.isPresent()) {
            generatorOpt = context.getUserSuppliedGenerator(node.getKlass());
        }

        if (generatorOpt.isPresent()) {
            final Generator<?> generator = generatorOpt.get();
            if (generator.isDelegating()) {
                final Class<?> targetType = ObjectUtils.defaultIfNull(generator.targetType(), node.getKlass());
                final Class<?> effectiveType = context.getSubtypeMapping(targetType);
                final Generator<?> delegate = generatorResolver.get(effectiveType).orElseGet(
                        () -> new InstantiatingGenerator(context, instantiator, effectiveType));

                generator.setDelegate(delegate);
            }
        }

        return generatorOpt;
    }

}
