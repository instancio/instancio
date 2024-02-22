import org.instancio.tests.jpms.spi.impl.JpmsInstancioServiceProviderImpl;

module org.instancio.tests.jpms.spi.impl {
    requires org.instancio.core;

    provides org.instancio.spi.InstancioServiceProvider
            with JpmsInstancioServiceProviderImpl;
}