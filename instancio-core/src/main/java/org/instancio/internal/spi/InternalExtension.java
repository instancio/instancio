/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.internal.spi;

import org.instancio.Node;
import org.instancio.documentation.InternalApi;
import org.instancio.internal.generator.InternalContainerHint;
import org.instancio.settings.Settings;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Function;

/**
 * Internal SPI used for extension projects.
 *
 * <p>This class is <b>not</b> part of the public API.
 *
 * @since 4.0.0
 */
@InternalApi
public interface InternalExtension {

    @Nullable
    default InternalContainerFactoryProvider getContainerFactoryProvider() {
        return null;
    }

    @Nullable
    default InternalNullSubstitutor getNullSubstitutor() {
        return null;
    }

    @Nullable
    default InternalGetterMethodFieldResolver getGetterMethodFieldResolver() {
        return null;
    }

    @Nullable
    default InternalNodeFilter getNodeFilter() {
        return null;
    }

    @Nullable
    default InternalAssignerSettingsProvider getAssignerSettingsProvider() {
        return null;
    }

    /**
     * Provides {@link Settings} overrides that control how values are assigned
     * to children of a specific target class. This allows internal SPIs
     * to provide settings without reliance on {@code instancio.properties}
     * which can be overridden by users.
     *
     * @since 6.0.0
     */
    interface InternalAssignerSettingsProvider {

        /**
         * Returns the {@link Settings} overrides to use when assigning
         * values to children of the given target class.
         *
         * @param targetClass the parent object's class being populated
         * @return settings overrides, or {@code null} if not applicable
         */
        @Nullable
        Settings getAssignerSettings(Class<?> targetClass);
    }

    /**
     * Determines how a node should be handled during graph processing.
     *
     * @since 6.0.0
     */
    interface InternalNodeFilter {

        /**
         * Outcome of evaluating a node.
         *
         * @since 6.0.0
         */
        enum Decision {

            /**
             * Node is retained in the graph as-is.
             */
            KEEP,

            /**
             * Node is retained but marked ignored; visible in {@code verbose()} output.
             */
            IGNORE,

            /**
             * Node is removed from the graph.
             */
            PRUNE
        }

        /**
         * Evaluates the given node and returns the appropriate {@link Decision}.
         *
         * @param node the node to evaluate
         * @return the decision for the node
         * @since 6.0.0
         */
        Decision resolve(Node node);
    }

    /**
     * An SPI for resolving a field from a method reference.
     *
     * @since 4.0.0
     */
    interface InternalGetterMethodFieldResolver {

        /**
         * Resolved the field from a getter method reference.
         *
         * <p>The arguments, {@code declaringClass} and {@code methodName},
         * are extracted from a method reference. For example, given a selector
         * {@code Select.field(Person::getAge)}, the target class
         * will be {@code Person} and the method name {@code getAge}.
         *
         * @param declaringClass class that declares the method
         * @param methodName     the method name extracted from method reference
         * @return resolved field or {@code null} if the field could not be resolved
         */
        @Nullable
        Field resolveField(Class<?> declaringClass, String methodName);
    }

    /**
     * Replaces a null value produced with a non-null substitute, if applicable.
     *
     * @since 6.0.0
     */
    interface InternalNullSubstitutor {

        /**
         * Returns a substitute for null, or {@code null} to keep the null result.
         *
         * @param node for which a null result was generated
         * @return substitute value, or {@code null} for no substitution
         */
        @Nullable
        Object substituteNull(Node node);
    }

    /**
     * An SPI that allows populating data structures using a method.
     *
     * @since 2.0.0
     */
    interface InternalContainerFactoryProvider {
        /**
         * Returns a function that converts a source object of type
         * {@code S} to the target type {@code T}.
         *
         * <p>The {@code typeArguments} is provided for containers that
         * require the type of element to be specified at construction time,
         * for example {@link java.util.EnumMap#EnumMap(Class)}.
         *
         * @param targetType    the type to be created
         * @param typeArguments type arguments of the source object, or an empty list
         *                      if the source object is not a parameterised type
         * @param <S>           source type
         * @param <T>           target type
         * @return conversion function, or {@code null} if not defined
         */
        @Nullable
        <S, T> Function<S, T> getMappingFunction(Class<T> targetType, List<Class<?>> typeArguments);

        /**
         * Returns {@code true} if the given {@code type} represents a container.
         * A container is a data structure that is populated using a method,
         * for example {@code Collection.add()} or {@code Optional.of()}
         * If a class is marked as a container, Instancio will not inspect
         * its fields since the object will not be populated via fields.
         *
         * <p>Container generators should return a {@link InternalContainerHint}
         * that specifies how the data structure should be populated.
         *
         * @param type to check
         * @return {@code true} if the type is a container, {@code false} otherwise
         * @see InternalContainerHint
         */
        boolean isContainer(Class<?> type);
    }
}
