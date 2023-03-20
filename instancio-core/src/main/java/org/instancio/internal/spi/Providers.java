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
package org.instancio.internal.spi;

import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.InstancioServiceProvider.GeneratorProvider;
import org.instancio.spi.InstancioServiceProvider.TypeInstantiator;
import org.instancio.spi.InstancioServiceProvider.TypeResolver;
import org.instancio.spi.ServiceProviderContext;

import java.util.List;

public final class Providers {

    private final List<ProviderEntry<GeneratorProvider>> generatorProviders;
    private final List<ProviderEntry<TypeResolver>> typeResolvers;
    private final List<ProviderEntry<TypeInstantiator>> typeInstantiators;

    public Providers(final List<InstancioServiceProvider> spList, ServiceProviderContext context) {
        spList.forEach(sp -> sp.init(context));
        generatorProviders = ProviderEntry.from(spList, InstancioServiceProvider::getGeneratorProvider);
        typeResolvers = ProviderEntry.from(spList, InstancioServiceProvider::getTypeResolver);
        typeInstantiators = ProviderEntry.from(spList, InstancioServiceProvider::getTypeInstantiator);
    }

    public List<ProviderEntry<GeneratorProvider>> getGeneratorProviders() {
        return generatorProviders;
    }

    public List<ProviderEntry<TypeResolver>> getTypeResolvers() {
        return typeResolvers;
    }

    public List<ProviderEntry<TypeInstantiator>> getTypeInstantiators() {
        return typeInstantiators;
    }
}
