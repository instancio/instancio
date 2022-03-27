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
import org.instancio.internal.GeneratorResolver;
import org.instancio.internal.GeneratorResult;
import org.instancio.internal.model.ModelContext;
import org.instancio.internal.model.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UsingGeneratorResolverHandler implements NodeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UsingGeneratorResolverHandler.class);

    private final ModelContext<?> context;
    private final GeneratorResolver generatorResolver;

    public UsingGeneratorResolverHandler(final ModelContext<?> context, final GeneratorResolver generatorResolver) {
        this.context = context;
        this.generatorResolver = generatorResolver;
    }

    @Override
    public Optional<GeneratorResult> getResult(final Node node) {
        final Class<?> effectiveType = context.getSubtypeMapping(node.getKlass());
        final Optional<Generator<?>> generatorOpt = generatorResolver.get(effectiveType);

        return generatorOpt.map(generator -> {
            LOG.trace("Using '{}' generator to create '{}'", generator.getClass().getSimpleName(), effectiveType.getName());
            final GeneratorResult result = GeneratorResult.create(generator.generate(), generator.getHints());
            LOG.trace("Generated {} using '{}' generator ", result, generator.getClass().getSimpleName());
            return result;
        });
    }
}
