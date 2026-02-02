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
package org.instancio.internal.spi;

import org.instancio.spi.InstancioServiceProvider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

public final class ProviderEntry<P> {
    private final Class<? extends InstancioServiceProvider> instancioProviderClass;
    private final P provider;

    private ProviderEntry(
            final Class<? extends InstancioServiceProvider> instancioProviderClass,
            final P provider) {

        this.instancioProviderClass = instancioProviderClass;
        this.provider = provider;
    }

    public static <P> List<ProviderEntry<P>> from(
            final List<InstancioServiceProvider> instancioServiceProviders,
            final Function<InstancioServiceProvider, P> fn) {

        if (instancioServiceProviders.isEmpty()) {
            return Collections.emptyList();
        }

        final List<ProviderEntry<P>> results = new ArrayList<>();
        for (InstancioServiceProvider it : instancioServiceProviders) {

            // Note: fn.apply() will invoke the corresponding "get provider"
            // method, for example, getGeneratorProvider(). The API contract
            // is to invoke each getter from InstancioServiceProvider
            // only once as these methods may contain potentially expensive
            // initialisation logic
            final P provider = fn.apply(it);

            if (provider != null) {
                results.add(new ProviderEntry<>(it.getClass(), provider));
            }
        }
        return Collections.unmodifiableList(results);
    }

    public Class<? extends InstancioServiceProvider> getInstancioProviderClass() {
        return instancioProviderClass;
    }

    public P getProvider() {
        return provider;
    }
}
