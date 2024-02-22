open module org.instancio.tests.jpms.spi {
    requires org.instancio.core;
    requires org.instancio.junit;

    requires org.assertj.core;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;

    uses org.instancio.spi.InstancioServiceProvider;
}