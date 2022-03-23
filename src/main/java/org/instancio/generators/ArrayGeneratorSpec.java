package org.instancio.generators;

import org.instancio.GeneratorSpec;

public interface ArrayGeneratorSpec<T> extends GeneratorSpec<T> {

    ArrayGeneratorSpec<T> minLength(int length);

    ArrayGeneratorSpec<T> maxLength(int length);

    ArrayGeneratorSpec<T> type(Class<?> type);
}
