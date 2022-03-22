package org.instancio.internal.reflection;

import java.util.Optional;

public interface ImplementationResolver {

    Optional<Class<?>> resolve(Class<?> interfaceClass);

}
