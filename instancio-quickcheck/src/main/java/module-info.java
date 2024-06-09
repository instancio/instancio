/**
 * @deprecated the {@code instancio-quickcheck} module is deprecated
 *             and will be removed in version 5.
 */
@Deprecated
module org.instancio.quickcheck {
  requires transitive org.instancio.core;

  requires org.junit.jupiter.api;
  requires org.junit.jupiter.engine;
  requires org.junit.platform.engine;
  requires org.slf4j;

  exports org.instancio.quickcheck.api;
  exports org.instancio.quickcheck.api.artbitrary;

  provides org.junit.platform.engine.TestEngine with org.instancio.quickcheck.internal.engine.InstancioQuickcheckTestEngine;
}