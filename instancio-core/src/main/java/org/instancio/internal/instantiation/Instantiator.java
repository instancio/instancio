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
package org.instancio.internal.instantiation;


import org.instancio.internal.util.ExceptionHandler;
import org.instancio.internal.util.Sonar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Instantiator {
    private static final Logger LOG = LoggerFactory.getLogger(Instantiator.class);

    private final InstantiationStrategy[] strategies = {
            new NoArgumentConstructorInstantiationStrategy(),
            new UnsafeInstantiationStrategy()
    };

    public <T> T instantiate(Class<T> klass) {
        for (InstantiationStrategy strategy : strategies) {
            final T instance = createInstance(klass, strategy);
            if (instance != null) {
                return instance;
            }
        }

        LOG.debug("Could not instantiate class '{}'", klass.getName());
        return null;
    }

    @SuppressWarnings(Sonar.CATCH_EXCEPTION_INSTEAD_OF_THROWABLE)
    private <T> T createInstance(final Class<T> klass, final InstantiationStrategy strategy) {
        try {
            return strategy.createInstance(klass);
        } catch (Throwable ex) { //NOPMD catches java.lang.InstantiationError
            ExceptionHandler.logException("'{}' failed instantiating {}",
                    ex, strategy.getClass().getSimpleName(), klass);
        }
        return null;
    }
}
