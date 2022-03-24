package org.instancio.internal.reflection.instantiation;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.objenesis.instantiator.ObjectInstantiator;

import java.util.HashMap;
import java.util.Map;

public class ObjenesisInstantiationStrategy implements InstantiationStrategy {

    private final Objenesis objenesis = new ObjenesisStd();
    private final Map<Class<?>, ObjectInstantiator<?>> cache = new HashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T createInstance(final Class<T> klass) {
        ObjectInstantiator<?> instantiator = cache.computeIfAbsent(klass, objenesis::getInstantiatorOf);
        return (T) instantiator.newInstance();
    }

}
