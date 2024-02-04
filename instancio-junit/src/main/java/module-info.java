module org.instancio.junit {
  requires transitive org.instancio.core;

  requires org.junit.jupiter.api;
  requires org.junit.jupiter.params;
  requires org.slf4j;

  exports org.instancio.junit;
}