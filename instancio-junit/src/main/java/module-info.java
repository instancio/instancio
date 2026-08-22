module org.instancio.junit {
  requires transitive org.instancio.core;

  requires org.jspecify;
  requires org.junit.jupiter.api;
  requires org.junit.platform.commons;

  exports org.instancio.junit;
}