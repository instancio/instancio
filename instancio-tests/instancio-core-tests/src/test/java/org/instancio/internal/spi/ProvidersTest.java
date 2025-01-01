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

import org.instancio.settings.Settings;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.ServiceProviderContext;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ProvidersTest {

    @Test
    void verifyProviderInit() {
        final InstancioServiceProviderImpl p1 = new InstancioServiceProviderImpl();
        final InstancioServiceProviderImpl p2 = new InstancioServiceProviderImpl();
        final ServiceProviderContext context = new InternalServiceProviderContext
                (Settings.create(), new DefaultRandom());

        final Providers providers = new Providers(Arrays.asList(p1, p2), context);

        assertThat(p1.initCount).isEqualTo(p2.initCount).isEqualTo(1);
        assertThat(p1.context).isSameAs(p2.context).isSameAs(context);

        assertThat(providers.getGeneratorProviders()).isEmpty();
        assertThat(providers.getTypeResolvers()).isEmpty();
        assertThat(providers.getTypeInstantiators()).isEmpty();
    }

    private static class InstancioServiceProviderImpl implements InstancioServiceProvider {
        int initCount;
        ServiceProviderContext context;

        @Override
        public void init(final ServiceProviderContext ctx) {
            initCount++;
            context = ctx;
        }
    }
}
