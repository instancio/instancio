module org.instancio.junit {
  requires transitive org.instancio.core;

  requires static org.junit.jupiter.params;

  requires org.junit.jupiter.api;
  requires org.slf4j;

  exports org.instancio.junit;
}