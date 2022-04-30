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
package org.instancio.internal.reflection.instantiation;


import org.instancio.util.Sonar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Instantiator {
    private static final Logger LOG = LoggerFactory.getLogger(Instantiator.class);

    private final List<InstantiationStrategy> strategies = new ArrayList<>();

    public Instantiator() {
        strategies.add(new NoArgumentConstructorInstantiationStrategy());
        strategies.add(new ObjenesisInstantiationStrategy());
    }

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
        } catch (Throwable ex) { //NOPMD
            LOG.trace("'{}' failed instantiating class '{}'", strategy.getClass().getSimpleName(), klass.getName(), ex);
        }
        return null;
    }
}
