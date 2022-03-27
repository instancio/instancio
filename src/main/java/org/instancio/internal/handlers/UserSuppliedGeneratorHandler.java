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
import org.instancio.internal.GeneratorMap;
import org.instancio.internal.GeneratorResult;
import org.instancio.internal.model.ModelContext;
import org.instancio.internal.model.Node;
import org.instancio.internal.reflection.instantiation.Instantiator;
import org.instancio.util.ObjectUtils;

import java.util.Optional;

public class UserSuppliedGeneratorHandler implements NodeHandler {

    private final ModelContext<?> context;
    private final GeneratorMap generatorMap;
    private final Instantiator instantiator;

    public UserSuppliedGeneratorHandler(final ModelContext<?> context,
                                        final GeneratorMap generatorMap,
                                        final Instantiator instantiator) {
        this.context = context;
        this.generatorMap = generatorMap;
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
        Generator<?> generator = null;
        if (node.getField() != null && context.getUserSuppliedFieldGenerators().containsKey(node.getField())) {
            generator = context.getUserSuppliedFieldGenerators().get(node.getField());
        } else if (context.getUserSuppliedClassGenerators().containsKey(node.getKlass())) {
            generator = context.getUserSuppliedClassGenerators().get(node.getKlass());
        }

        if (generator != null && generator.isDelegating()) {
            final Class<?> targetType = ObjectUtils.defaultIfNull(generator.targetType(), node.getKlass());
            final Class<?> effectiveType = context.getSubtypeMapping(targetType);
            Generator<?> delegate = generatorMap.get(effectiveType);
            if (delegate == null) {
                delegate = new InstantiatingGenerator(context, instantiator, effectiveType);
            }

            generator.setDelegate(delegate);
        }

        return Optional.ofNullable(generator);
    }

}
