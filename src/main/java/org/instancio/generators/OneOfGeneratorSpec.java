package org.instancio.generators;

import org.instancio.GeneratorSpec;

public interface OneOfGeneratorSpec<T> extends GeneratorSpec<T> {

    OneOfGeneratorSpec<T> oneOf(T[] values);
}
