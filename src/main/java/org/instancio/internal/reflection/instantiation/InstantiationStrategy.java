package org.instancio.internal.reflection.instantiation;

public interface InstantiationStrategy {

    <T> T createInstance(Class<T> klass);
}
