module org.instancio.core {
    requires org.slf4j;

    requires static com.fasterxml.jackson.databind;
    requires static jakarta.persistence;
    requires static jakarta.validation;
    requires static java.persistence;
    requires static java.validation;
    requires static java.sql;
    requires static java.xml;
    requires static jdk.unsupported;
    requires static org.jetbrains.annotations;
    requires static org.hibernate.validator;
    requires static org.apache.groovy;

    exports org.instancio;
    exports org.instancio.documentation;
    exports org.instancio.exception;
    exports org.instancio.feed;
    exports org.instancio.generator;
    exports org.instancio.generator.hints;
    exports org.instancio.generator.specs;
    exports org.instancio.generator.specs.can;
    exports org.instancio.generator.specs.pol;
    exports org.instancio.generator.specs.usa;
    exports org.instancio.generator.specs.bra;
    exports org.instancio.generator.specs.rus;
    exports org.instancio.generators;
    exports org.instancio.generators.can;
    exports org.instancio.generators.pol;
    exports org.instancio.generators.usa;
    exports org.instancio.generators.bra;
    exports org.instancio.generators.rus;
    exports org.instancio.settings;
    exports org.instancio.spi;

    exports org.instancio.internal to org.instancio.junit, org.instancio.guava;
    exports org.instancio.internal.generator to org.instancio.guava;
    exports org.instancio.internal.generator.domain.internet to org.instancio.guava;
    exports org.instancio.internal.generator.util to org.instancio.guava;
    exports org.instancio.internal.spi to org.instancio.guava;
    exports org.instancio.internal.util to org.instancio.guava, org.instancio.junit;
    exports org.instancio.support to org.instancio.junit;

    uses org.instancio.spi.InstancioServiceProvider;

    // only for other instancio modules
    uses org.instancio.internal.spi.InternalServiceProvider;
}
