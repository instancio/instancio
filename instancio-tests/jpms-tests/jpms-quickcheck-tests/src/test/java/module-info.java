open module org.instancio.tests.jpms.quikcheck {
    requires org.instancio.core;
    requires org.instancio.quickcheck;

    requires org.assertj.core;
    requires org.junit.jupiter.api;
    requires org.junit.jupiter.engine;
}