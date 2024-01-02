/*
 * Copyright 2022-2024 the original author or authors.
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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class ProviderEntryTest {

    private static final GeneratorProvider NOOP_GENERATOR_PROVIDER = (node, generators) -> null;
    private static final TypeResolver NOOP_TYPE_RESOLVER = type -> null;
    private static final TypeInstantiator NOOP_TYPE_INSTANTIATOR = type -> null;

    @Test
    void from() {
        final InstancioServiceProvider provider = new InstancioServiceProvider() {
            @Override
            public TypeResolver getTypeResolver() {
                return NOOP_TYPE_RESOLVER;
            }

            @Override
            public TypeInstantiator getTypeInstantiator() {
                return NOOP_TYPE_INSTANTIATOR;
            }
        };

        final List<InstancioServiceProvider> serviceProviders = Collections.singletonList(provider);

        final List<ProviderEntry<TypeResolver>> typeResolvers = ProviderEntry.from(
                serviceProviders, InstancioServiceProvider::getTypeResolver);

        assertThat(typeResolvers).hasSize(1);
        assertThat(typeResolvers.get(0).getProvider()).isSameAs(NOOP_TYPE_RESOLVER);
        assertThat(typeResolvers.get(0).getInstancioProviderClass()).isSameAs(provider.getClass());

        final List<ProviderEntry<TypeInstantiator>> typeInstantiators = ProviderEntry.from(
                serviceProviders, InstancioServiceProvider::getTypeInstantiator);

        assertThat(typeInstantiators).hasSize(1);
        assertThat(typeInstantiators.get(0).getProvider()).isSameAs(NOOP_TYPE_INSTANTIATOR);
        assertThat(typeInstantiators.get(0).getInstancioProviderClass()).isSameAs(provider.getClass());

        assertThat(ProviderEntry.from(serviceProviders, InstancioServiceProvider::getGeneratorProvider))
                .isEmpty();
    }

    @Test
    void shouldInvokeFunctionOnlyOncePerInstancioServiceProvider() {
        final InstancioServiceProvider provider1 = createProvider();
        final InstancioServiceProvider provider2 = createProvider();
        final List<InstancioServiceProvider> serviceProviders = Arrays.asList(provider1, provider2);

        final CountingFunction<InstancioServiceProvider, ?> fn = new CountingFunction<>(
                InstancioServiceProvider::getGeneratorProvider);

        ProviderEntry.from(serviceProviders, fn);

        assertThat(fn.count).isEqualTo(2);
    }

    @NotNull
    private static InstancioServiceProvider createProvider() {
        return new InstancioServiceProvider() {
            @Override
            public GeneratorProvider getGeneratorProvider() {
                return NOOP_GENERATOR_PROVIDER;
            }

            @Override
            public TypeResolver getTypeResolver() {
                return NOOP_TYPE_RESOLVER;
            }

            @Override
            public TypeInstantiator getTypeInstantiator() {
                return NOOP_TYPE_INSTANTIATOR;
            }
        };
    }

    // There's probably a better way to do this
    private static class CountingFunction<T, R> implements Function<T, R> {
        private final Function<T, R> fn;
        private int count;

        CountingFunction(final Function<T, R> fn) {
            this.fn = fn;
        }

        @Override
        public R apply(final T t) {
            count++;
            return fn.apply(t);
        }
    }
}
