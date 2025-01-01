/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.internal.util.ServiceLoaders;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.InstancioServiceProvider.AnnotationProcessor;
import org.instancio.spi.InstancioServiceProvider.GeneratorProvider;
import org.instancio.spi.InstancioServiceProvider.SetterMethodResolver;
import org.instancio.spi.InstancioServiceProvider.TypeInstantiator;
import org.instancio.spi.InstancioServiceProvider.TypeResolver;
import org.instancio.spi.ServiceProviderContext;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.List;

public final class Providers {

    private final List<ProviderEntry<GeneratorProvider>> generatorProviders;
    private final List<ProviderEntry<TypeResolver>> typeResolvers;
    private final List<ProviderEntry<TypeInstantiator>> typeInstantiators;
    private final List<ProviderEntry<SetterMethodResolver>> setterMethodResolvers;
    private final List<ProviderEntry<AnnotationProcessor>> annotationProcessors;

    public Providers(final ServiceProviderContext context) {
        this(ServiceLoaders.loadAll(InstancioServiceProvider.class), context);
    }

    @VisibleForTesting
    Providers(final List<InstancioServiceProvider> spList, final ServiceProviderContext context) {
        spList.forEach(sp -> sp.init(context));

        generatorProviders = ProviderEntry.from(spList, InstancioServiceProvider::getGeneratorProvider);
        typeResolvers = ProviderEntry.from(spList, InstancioServiceProvider::getTypeResolver);
        typeInstantiators = ProviderEntry.from(spList, InstancioServiceProvider::getTypeInstantiator);
        setterMethodResolvers = ProviderEntry.from(spList, InstancioServiceProvider::getSetterMethodResolver);
        annotationProcessors = ProviderEntry.from(spList, InstancioServiceProvider::getAnnotationProcessor);
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

    public List<ProviderEntry<SetterMethodResolver>> getSetterMethodResolvers() {
        return setterMethodResolvers;
    }

    public List<ProviderEntry<AnnotationProcessor>> getAnnotationProcessors() {
        return annotationProcessors;
    }
}
