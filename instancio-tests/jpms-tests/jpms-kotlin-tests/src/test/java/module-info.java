open module org.instancio.tests.jpms.kotlin {
    requires org.instancio.junit;
    requires org.instancio.kotlin;

    requires kotlin.reflect;
    requires kotlin.stdlib;
    requires org.assertj.core;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
}
