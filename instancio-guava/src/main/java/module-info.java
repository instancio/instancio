module org.instancio.guava {
    requires transitive org.instancio.core;

    requires com.google.common;

    exports org.instancio.guava;
    exports org.instancio.guava.generator.specs;

    provides org.instancio.spi.InstancioServiceProvider with org.instancio.guava.internal.spi.GuavaProvider;
    provides org.instancio.internal.spi.InternalServiceProvider with org.instancio.guava.internal.spi.GuavaInternalServiceProvider;
}