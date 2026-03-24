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
package org.instancio.internal.generator;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.generators.BuiltInGenerators;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.spi.InstancioServiceProvider.GeneratorProvider;
import org.instancio.spi.InstancioSpiException;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static java.util.Objects.requireNonNullElse;

public class SpiGeneratorResolver {
    private static final Logger LOG = LoggerFactory.getLogger(SpiGeneratorResolver.class);

    private final GeneratorContext generatorContext;
    private final Generators generators;
    private final GeneratorResolver generatorResolver;
    private final List<ProviderEntry<GeneratorProvider>> providerEntries;
    private final AfterGenerate afterGenerate;

    public SpiGeneratorResolver(
            final ModelContext modelContext,
            final GeneratorContext generatorContext,
            final GeneratorResolver generatorResolver) {

        this.generatorContext = generatorContext;
        this.generators = new BuiltInGenerators(generatorContext);
        this.generatorResolver = generatorResolver;
        this.providerEntries = modelContext.getServiceProviders().getGeneratorProviders();
        this.afterGenerate = modelContext.getSettings().get(Keys.AFTER_GENERATE_HINT);
    }

    @Nullable
    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    public Generator<?> getSpiGenerator(final InternalNode node) {
        for (ProviderEntry<GeneratorProvider> entry : providerEntries) {
            final GeneratorSpec<?> spec = entry.getProvider().getGenerator(node, generators);

            if (spec != null) {
                validateSpec(entry, spec);
                LOG.trace("Custom generator '{}' found for {}", spec.getClass().getName(), node);

                final Generator<?> generator = processGenerator((Generator<?>) spec, node);
                generator.init(generatorContext);
                return GeneratorDecorator.decorateIfNullAfterGenerate(generator, afterGenerate);
            }
        }
        return null;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    private Generator<?> processGenerator(final Generator<?> generator, final InternalNode node) {
        return DelegatingGeneratorProcessor.process(generator, node, internalHint -> {
            final Class<?> targetClass = requireNonNullElse(internalHint.targetClass(), node.getTargetClass());
            return generatorResolver.getCachedBuiltInGenerator(targetClass);
        });
    }

    private static void validateSpec(
            final ProviderEntry<GeneratorProvider> entry,
            final GeneratorSpec<?> spec) {

        if (spec instanceof Generator<?>) {
            return;
        }
        throw new InstancioSpiException(String.format("The GeneratorSpec %s returned by %s must implement %s",
                spec.getClass(), entry.getInstancioProviderClass(), Generator.class));
    }
}
