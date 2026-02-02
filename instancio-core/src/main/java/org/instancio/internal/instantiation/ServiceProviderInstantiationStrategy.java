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
package org.instancio.internal.instantiation;

import org.instancio.internal.spi.ProviderEntry;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.InstancioSpiException;

import java.util.List;

/**
 * Attempts to instantiate classes via the SPI.
 */
class ServiceProviderInstantiationStrategy implements InstantiationStrategy {

    private final List<ProviderEntry<InstancioServiceProvider.TypeInstantiator>> providerEntries;

    ServiceProviderInstantiationStrategy(final List<ProviderEntry<InstancioServiceProvider.TypeInstantiator>> providerEntries) {
        this.providerEntries = providerEntries;
    }

    @Override
    public <T> T createInstance(final Class<T> klass) {
        for (ProviderEntry<InstancioServiceProvider.TypeInstantiator> entry : providerEntries) {
            final Object result = entry.getProvider().instantiate(klass);

            if (result != null) {
                if (!klass.isAssignableFrom(result.getClass())) {
                    throw new InstancioSpiException(String.format(
                            "%s instantiated an object of invalid type:%n" +
                                    " -> %s%n" +
                                    "Expected an instance of:%n" +
                                    " -> %s",
                            entry.getInstancioProviderClass(), result.getClass(), klass));
                }
                return klass.cast(result);
            }
        }
        return null;
    }
}
