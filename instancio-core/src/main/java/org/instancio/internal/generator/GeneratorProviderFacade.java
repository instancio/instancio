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
package org.instancio.internal.generator;

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.InstancioSpiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

/**
 * Resolves generators provided by the SPI.
 */
class GeneratorProviderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorProviderFacade.class);

    private final GeneratorContext context;
    private final Generators generators;
    private final List<ProviderEntry<InstancioServiceProvider.GeneratorProvider>> providerEntries;
    private final AfterGenerate afterGenerate;

    GeneratorProviderFacade(
            final GeneratorContext context,
            final List<ProviderEntry<InstancioServiceProvider.GeneratorProvider>> providerEntries) {

        this.context = context;
        this.generators = new Generators(context);
        this.afterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        this.providerEntries = providerEntries;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    Optional<Generator<?>> getGenerator(final InternalNode node) {
        Generator<?> result = resolveViaSPI(node);
        return Optional.ofNullable(result);
    }

    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    private Generator<?> resolveViaSPI(final InternalNode node) {

        for (ProviderEntry<InstancioServiceProvider.GeneratorProvider> entry : providerEntries) {
            final GeneratorSpec<?> spec = entry.getProvider().getGenerator(node, generators);

            if (spec != null) {
                validateSpec(entry, spec);

                final Generator<?> generator = (Generator<?>) spec;
                LOG.trace("Custom generator '{}' found for {}", generator.getClass().getName(), node);
                generator.init(context);
                return GeneratorDecorator.decorateIfNullAfterGenerate(generator, afterGenerate);
            }
        }
        return null;
    }

    private static void validateSpec(
            final ProviderEntry<InstancioServiceProvider.GeneratorProvider> entry,
            final GeneratorSpec<?> spec) {

        if (spec instanceof Generator<?>) {
            return;
        }
        throw new InstancioSpiException(String.format("The GeneratorSpec %s returned by %s must implement %s",
                spec.getClass(), entry.getInstancioProviderClass(), Generator.class));
    }
}
