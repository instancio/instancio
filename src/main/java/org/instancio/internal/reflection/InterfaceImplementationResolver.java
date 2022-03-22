package org.instancio.internal.reflection;

import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.Set;

public class InterfaceImplementationResolver implements ImplementationResolver {
    private static final Logger LOG = LoggerFactory.getLogger(InterfaceImplementationResolver.class);

    @Override
    public Optional<Class<?>> resolve(Class<?> interfaceClass) {
        try {
            Reflections reflections = new Reflections(
                    new ConfigurationBuilder()
                            .setUrls(ClasspathHelper.forJavaClassPath()));


            final Set<Class<?>> implementors = reflections.getSubTypesOf((Class<Object>) interfaceClass);

            if (implementors.size() != 1) {
                LOG.debug("Found {} implementors for class {}: {}. Will not instantiate.",
                        implementors.size(), interfaceClass.getName(), implementors);

                return Optional.empty();
            }

            return Optional.of(implementors.iterator().next());


        } catch (Exception e) {
            LOG.debug("Error resolving interface '{}' implementation", interfaceClass.getName());
            return Optional.empty();
        }
    }
}
