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
import org.instancio.internal.GeneratorSpecProcessor;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.GeneratorResolver;
import org.instancio.internal.generator.GeneratorResult;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.settings.Keys;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class UsingGeneratorResolverHandler implements NodeHandler {
    private static final Logger LOG = LoggerFactory.getLogger(UsingGeneratorResolverHandler.class);

    private final ModelContext<?> context;
    private final GeneratorResolver generatorResolver;
    private final GeneratorSpecProcessor beanValidationProcessors;
    private final GeneratedValuePostProcessor stringPostProcessor;

    public UsingGeneratorResolverHandler(
            final ModelContext<?> context,
            final GeneratorResolver generatorResolver,
            final GeneratorSpecProcessor beanValidationProcessors) {

        this.context = context;
        this.generatorResolver = generatorResolver;
        this.stringPostProcessor = new StringPrefixingPostProcessor(
                context.getSettings().get(Keys.STRING_FIELD_PREFIX_ENABLED));
        this.beanValidationProcessors = beanValidationProcessors;
    }

    @NotNull
    @Override
    public GeneratorResult getResult(@NotNull final InternalNode node) {
        final Optional<Generator<?>> generatorOpt = generatorResolver.get(node);

        if (!generatorOpt.isPresent()) {
            return GeneratorResult.emptyResult();
        }

        final Generator<?> generator = generatorOpt.get();
        beanValidationProcessors.process(generator, node);

        final Object value = generator.generate(context.getRandom());
        final Object processed = stringPostProcessor.process(value, node, generator);
        final GeneratorResult result = GeneratorResult.create(processed, generator.hints());

        LOG.trace("Generated '{}' using {}", result, generator.getClass());
        return result;
    }
}
