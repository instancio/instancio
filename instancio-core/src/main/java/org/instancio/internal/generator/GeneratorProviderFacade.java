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
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.spi.GeneratorProvider;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.InstancioSpiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

class GeneratorProviderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorProviderFacade.class);

    private final GeneratorContext context;
    private final List<GeneratorProvider> generatorProviders;
    private final List<ProviderEntry<InstancioServiceProvider.GeneratorProvider>> providerEntries;
    private final AfterGenerate afterGenerate;

    GeneratorProviderFacade(
            final GeneratorContext context,
            final List<GeneratorProvider> generatorProviders,
            final List<ProviderEntry<InstancioServiceProvider.GeneratorProvider>> providerEntries) {

        this.context = context;
        this.generatorProviders = generatorProviders;
        this.afterGenerate = context.getSettings().get(Keys.AFTER_GENERATE_HINT);
        this.providerEntries = providerEntries;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    Optional<Generator<?>> getGenerator(final Class<?> forClass) {
        Generator<?> result = resolveViaNewSPI(forClass);
        if (result == null) {
            result = resolveViaDeprecatedSPI(forClass);
        }
        return Optional.ofNullable(result);
    }

    private Generator<?> resolveViaDeprecatedSPI(final Class<?> forClass) {
        for (GeneratorProvider provider : generatorProviders) {
            final Generator<?> generator = provider.getGenerators(context).get(forClass);
            if (generator != null) {
                LOG.trace("Custom generator '{}' found for {}", generator.getClass().getName(), forClass);
                return GeneratorDecorator.decorate(generator, afterGenerate);
            }
        }
        return null;
    }

    @SuppressWarnings("PMD.AvoidBranchingStatementAsLastInLoop")
    private Generator<?> resolveViaNewSPI(final Class<?> forClass) {
        for (ProviderEntry<InstancioServiceProvider.GeneratorProvider> entry : providerEntries) {
            final Class<?> generatorClass = entry.getProvider().getGenerator(forClass);
            if (generatorClass == null) {
                continue;
            }

            if (!Generator.class.isAssignableFrom(generatorClass)) {
                throw new InstancioSpiException(String.format(
                        "%s returned an invalid generator class:%n" +
                                " -> %s%n" +
                                "The class does not implement the %s interface",
                        entry.getInstancioProviderClass(), generatorClass.getName(), Generator.class));
            }

            final Generator<?> generator = GeneratorUtil.instantiateSpiGenerator(generatorClass, context);
            LOG.trace("SPI generator {} found for {}", generator, forClass);
            return GeneratorDecorator.decorate(generator, afterGenerate);
        }
        return null;
    }
}
