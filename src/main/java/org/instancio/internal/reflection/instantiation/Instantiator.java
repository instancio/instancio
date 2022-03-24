package org.instancio.internal.reflection.instantiation;


import org.instancio.exception.InstancioException;
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

        throw new InstancioException(String.format("Failed instantiating class '%s'", klass.getName()));
    }

    @SuppressWarnings("java:S1181")
    private <T> T createInstance(final Class<T> klass, final InstantiationStrategy strategy) {
        try {
            return strategy.createInstance(klass);
        } catch (Throwable ex) {
            LOG.trace("'{}' failed instantiating class '{}'", strategy.getClass().getSimpleName(), klass.getName(), ex);
        }
        return null;
    }
}
