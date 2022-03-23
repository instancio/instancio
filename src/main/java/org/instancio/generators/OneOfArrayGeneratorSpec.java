package org.instancio.generators;

import org.instancio.GeneratorSpec;

public interface OneOfArrayGeneratorSpec<T> extends GeneratorSpec<T> {

    OneOfArrayGeneratorSpec<T> oneOf(T[] values);
}
