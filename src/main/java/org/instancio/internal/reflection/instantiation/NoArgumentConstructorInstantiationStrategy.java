package org.instancio.internal.reflection.instantiation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;

public class NoArgumentConstructorInstantiationStrategy implements InstantiationStrategy {
    private static final Logger LOG = LoggerFactory.getLogger(NoArgumentConstructorInstantiationStrategy.class);

    @Override
    @SuppressWarnings({"unchecked", "java:S3011"})
    public <T> T createInstance(final Class<T> klass) {
        try {
            final Constructor<?> ctor = klass.getDeclaredConstructor();
            ctor.setAccessible(true);
            return (T) ctor.newInstance();
        } catch (Exception ex) {
            throw new InstantiationStrategyException("Error instantiating " + klass, ex);
        }
    }

}
