import org.instancio.guava.internal.spi.GuavaInternalExtension;
import org.instancio.guava.internal.spi.GuavaProvider;
import org.instancio.internal.spi.InternalExtension;
import org.instancio.spi.InstancioServiceProvider;

module org.instancio.guava {
    requires transitive org.instancio.core;

    requires com.google.common;
    requires org.jspecify;

    exports org.instancio.guava;
    exports org.instancio.guava.generator.specs;

    provides InstancioServiceProvider with GuavaProvider;
    provides InternalExtension with GuavaInternalExtension;
}