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

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.settings.InternalInstantiationStrategies;
import org.instancio.internal.spi.ProviderEntry;
import org.instancio.internal.util.ExceptionUtils;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;
import org.instancio.spi.InstancioServiceProvider.TypeInstantiator;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public final class Instantiator {
    private static final Logger LOG = LoggerFactory.getLogger(Instantiator.class);

    private final ServiceProviderInstanceCreator serviceProviderInstanceCreator;
    private final List<InstanceCreator> instanceCreators;

    public Instantiator(
            final List<ProviderEntry<TypeInstantiator>> typeInstantiators,
            final InstantiationStrategies instantiationStrategies) {

        this.serviceProviderInstanceCreator = new ServiceProviderInstanceCreator(typeInstantiators);
        this.instanceCreators = buildCreators((InternalInstantiationStrategies) instantiationStrategies);
    }

    /**
     * Builds the creators from the configured setting. The constructor-based
     * strategies are handled by the engine, which resolves a constructor while
     * building the node tree; only {@code BYPASS_CONSTRUCTOR} is handled here.
     */
    private static List<InstanceCreator> buildCreators(
            final InternalInstantiationStrategies instantiationStrategies) {

        final List<InstanceCreator> creators = new ArrayList<>();

        if (instantiationStrategies.contains(InstantiationStrategy.BYPASS_CONSTRUCTOR)) {
            creators.add(UnsafeInstanceCreator.getInstance());
            creators.add(ReflectionFactoryInstanceCreator.getInstance());
        }
        return creators;
    }

    /**
     * Instantiates the class via the SPI, if a registered {@link TypeInstantiator}
     * handles it. Unlike the fallback creators, an error raised by an SPI
     * provider is propagated rather than suppressed, since it originates
     * from user-supplied code.
     */
    @Nullable
    public <T> T instantiateViaSpi(final Class<T> klass) {
        return serviceProviderInstanceCreator.createInstance(klass);
    }

    @Nullable
    public <T> T instantiate(final Class<T> klass) {
        final T spiInstance = instantiateViaSpi(klass);
        if (spiInstance != null) {
            return spiInstance;
        }

        for (InstanceCreator creator : instanceCreators) {
            final T instance = createInstance(klass, creator);
            if (instance != null) {
                return instance;
            }
        }

        LOG.debug("Could not instantiate class '{}'", klass.getName());
        return null;
    }

    @Nullable
    @SuppressWarnings(Sonar.CATCH_EXCEPTION_INSTEAD_OF_THROWABLE)
    private <T> T createInstance(final Class<T> klass, final InstanceCreator creator) {
        try {
            LOG.trace("{}: attempting to instantiate {}", creator.getClass(), klass);
            return creator.createInstance(klass);
        } catch (InstancioApiException ex) {
            throw ex;
        } catch (Throwable ex) { // catches java.lang.InstantiationError
            // NOTE: InstanceCreators (ReflectionFactory, Unsafe) are not
            // subject to ON_CONSTRUCTOR_ERROR, therefore errors are suppressed
            // so that the next creator can be attempted.
            ExceptionUtils.logException("{}: failed instantiating {}",
                    ex, creator.getClass().getSimpleName(), klass);
        }
        return null;
    }
}
