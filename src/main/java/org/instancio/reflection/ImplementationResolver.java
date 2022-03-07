package org.instancio.reflection;

import java.util.Optional;

public interface ImplementationResolver {

    Optional<Class<?>> resolve(Class<?> interfaceClass);

}
