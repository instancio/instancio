package org.instancio.benchmark.spi;

import org.instancio.spi.InstancioServiceProvider;

public class SampleInstancioServiceProvider implements InstancioServiceProvider {

    @Override
    public GeneratorProvider getGeneratorProvider() {
        return (node, generators) -> null;
    }

    @Override
    public SetterMethodResolver getSetterMethodResolver() {
        return node -> null;
    }
}
