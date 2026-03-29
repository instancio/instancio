import org.instancio.protobuf.internal.spi.ProtoInternalServiceProvider;
import org.instancio.protobuf.internal.spi.ProtoServiceProvider;

module org.instancio.protobuf {
    requires transitive org.instancio.core;

    requires com.google.protobuf;
    requires org.jspecify;

    exports org.instancio.protobuf;
    exports org.instancio.protobuf.generator.specs;

    provides org.instancio.spi.InstancioServiceProvider with ProtoServiceProvider;
    provides org.instancio.internal.spi.InternalServiceProvider with ProtoInternalServiceProvider;
}