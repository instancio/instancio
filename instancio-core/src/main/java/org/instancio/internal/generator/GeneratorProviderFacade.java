/*
 * Copyright 2022 the original author or authors.
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

import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.misc.GeneratorDecorator;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.spi.GeneratorProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

class GeneratorProviderFacade {

    private static final Logger LOG = LoggerFactory.getLogger(GeneratorProviderFacade.class);

    private final Map<Class<?>, Generator<?>> cache = new ConcurrentHashMap<>();
    private final GeneratorContext context;
    private final List<GeneratorProvider> generatorProviders;

    GeneratorProviderFacade(final GeneratorContext context, final List<GeneratorProvider> generatorProviders) {
        this.context = context;
        this.generatorProviders = generatorProviders;
    }

    @SuppressWarnings(Sonar.GENERIC_WILDCARD_IN_RETURN)
    Optional<Generator<?>> getGenerator(final Class<?> forClass) {
        if (cache.containsKey(forClass)) {
            return Optional.of(cache.get(forClass));
        }

        for (GeneratorProvider provider : generatorProviders) {
            final Generator<?> generator = provider.getGenerators(context).get(forClass);
            if (generator != null) {
                LOG.trace("Custom generator '{}' found for {}", generator.getClass().getName(), forClass);

                final Generator<?> decorated = GeneratorDecorator.decorate(
                        generator, context.getSettings().get(Keys.AFTER_GENERATE_HINT));

                cache.put(forClass, decorated);
                return Optional.of(decorated);
            }
        }
        return Optional.empty();
    }

}
