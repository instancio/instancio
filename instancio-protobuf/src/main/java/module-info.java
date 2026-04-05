import org.instancio.internal.spi.InternalExtension;
import org.instancio.protobuf.internal.spi.ProtoInternalExtension;
import org.instancio.protobuf.internal.spi.ProtoServiceProvider;
import org.instancio.spi.InstancioServiceProvider;

module org.instancio.protobuf {
    requires transitive org.instancio.core;

    requires com.google.protobuf;
    requires org.jspecify;

    exports org.instancio.protobuf;
    exports org.instancio.protobuf.generator.specs;

    provides InstancioServiceProvider with ProtoServiceProvider;
    provides InternalExtension with ProtoInternalExtension;
}