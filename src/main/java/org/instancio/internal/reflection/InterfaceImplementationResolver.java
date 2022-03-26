/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
