package org.instancio.reflection;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Collections;
import java.util.Set;

public class InterfaceImplementationResolver implements ImplementationResolver {

    @Override
    public Set<Class<?>> resolve(Class<?> interfaceClass) {
        try {
            Reflections reflections = new Reflections(
                    new ConfigurationBuilder()
                            .setUrls(ClasspathHelper.forJavaClassPath()));


            return reflections.getSubTypesOf((Class<Object>) interfaceClass);

        } catch (Exception e) {
            return Collections.emptySet();
        }
    }
}
