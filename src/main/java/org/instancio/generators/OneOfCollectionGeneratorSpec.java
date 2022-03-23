package org.instancio.generators;

import org.instancio.GeneratorSpec;

import java.util.Collection;

public interface OneOfCollectionGeneratorSpec<T> extends GeneratorSpec<T> {

    OneOfCollectionGeneratorSpec<T> oneOf(Collection<T> values);
}
