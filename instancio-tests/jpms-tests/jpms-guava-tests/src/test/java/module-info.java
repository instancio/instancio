open module org.instancio.tests.jpms.guava {
    requires org.instancio.core;
    requires org.instancio.junit;
    requires org.instancio.guava;

    requires com.google.common;

    requires org.assertj.core;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
}