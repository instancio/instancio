package org.instancio.reflection;

import java.util.Set;

public interface ImplementationResolver {

    Set<Class<?>> resolve(Class<?> interfaceClass);

}
