/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.spi;

import org.instancio.InstancioApi;
import org.instancio.Node;
import org.instancio.TargetSelector;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generators.Generators;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.lang.reflect.Method;
import java.util.ServiceLoader;

/**
 * Instancio Service Provider Interface for providing custom:
 *
 * <ul>
 *   <li>generator mappings via {@link #getGeneratorProvider()}</li>
 *   <li>subtype mappings via {@link #getTypeResolver()}</li>
 *   <li>class instantiation logic via {@link #getTypeInstantiator()}</li>
 *   <li>setter resolution via {@link #getSetterMethodResolver()}</li>
 * </ul>
 *
 * <p>All of the above are {@code default} methods that return {@code null}.
 * Implementations of this class can override one or more methods as needed.
 * Instancio will invoke each of the above methods only once, therefore,
 * these methods may contain initialisation logic.
 *
 * <p>This class uses the {@link ServiceLoader} mechanism. Therefore,
 * implementations must be registered explicitly.
 * <ul>
 *   <li>
 *     for a module using the Java module system by adding
 *     {@code provides org.instancio.spi.InstancioServiceProvider with fully.qualified.ImplementationName;}
 *   </li>
 *   <li>
 *     otherwise in non-modular code or when read by non-modular code,
 *     by placing a file named {@code org.instancio.spi.InstancioServiceProvider} under
 *    {@code /META-INF/services}; The file must contain the fully-qualified name
 *     of the implementing class.
 *   </li>
 * </ul>
 *
 * <p><b>Note:</b> only {@link InstancioServiceProvider} (and not the interfaces
 * defined within it) can be registered via the {@code ServiceLoader}.
 *
 * @since 2.11.0
 */
public interface InstancioServiceProvider {

    /**
     * An optional method that can be used by implementations
     * that require access to the context information.
     *
     * @param context containing additional information
     * @since 2.12.0
     */
    @ExperimentalApi
    default void init(ServiceProviderContext context) {
    }

    /**
     * Returns a {@code GeneratorProvider} implementation.
     *
     * <p>Instancio will automatically use generators configured using
     * the provider. As an example, consider the following use-case where
     * {@code Person} objects need to be created with valid phone numbers.
     * To achieve this, a custom {@code Phone} generator is supplied:
     *
     * <pre>{@code
     *   Generator<Phone> phoneGenerator = createPhoneGenerator();
     *
     *   Person person = Instancio.of(Person.class)
     *      .supply(all(Phone.class), phoneGenerator)
     *      .create();
     * }</pre>
     *
     * <p>To avoid specifying the generator manually, the {@code Phone}
     * generator can be registered using the {@code GeneratorProvider}.
     * Once registered, the {@code Person} can be created without specifying
     * the generator explicitly:
     *
     * <pre>{@code
     *   Person person = Instancio.create(Person.class); // uses Phone generator
     * }</pre>
     *
     * @return a custom generator provider, or {@code null} if not required
     * @since 2.11.0
     */
    default GeneratorProvider getGeneratorProvider() {
        return null;
    }

    /**
     * Returns a {@code TypeResolver} implementation.
     *
     * @return a custom type resolver, or {@code null} if not required
     * @since 2.11.0
     */
    default TypeResolver getTypeResolver() {
        return null;
    }

    /**
     * Returns a {@code TypeInstantiator} implementation.
     *
     * @return a custom type instantiator, or {@code null} if not required
     * @since 2.11.0
     */
    default TypeInstantiator getTypeInstantiator() {
        return null;
    }

    /**
     * Returns a {@code SetterMethodResolver} implementation.
     *
     * @return a custom setter method resolver, or {@code null} if not required
     * @since 3.2.0
     */
    @ExperimentalApi
    default SetterMethodResolver getSetterMethodResolver() {
        return null;
    }

    /**
     * Provides custom {@link Generator} classes.
     *
     * <p>An implementation of this interface can be returned
     * via the {@link #getGeneratorProvider()} method.
     *
     * @since 2.11.0
     */
    interface GeneratorProvider {

        /**
         * Returns a generator spec for the specified {@code node}.
         * The returned spec must also implement the {@link Generator} interface.
         *
         * <p>If the implementation does not define a generator for the given
         * node, then a {@code null} can be returned.
         *
         * @param generators provides access to built-in generators
         * @param node       for which to return a generator
         * @return generator spec for the given {@code node}, or {@code null}
         * @throws InstancioSpiException if the returned generator spec
         *                               does not implement {@link Generator}
         * @since 2.11.0
         */
        GeneratorSpec<?> getGenerator(Node node, Generators generators);
    }

    /**
     * Resolves subtype based on a given class.
     *
     * <p>An implementation of this interface can be returned
     * via the {@link #getTypeResolver()} method.
     *
     * @since 2.11.0
     */
    interface TypeResolver {

        /**
         * Returns a subtype for the given {@code type}.
         *
         * <p>Similar functionality can be achieved using:
         *
         * <ul>
         *   <li>{@link InstancioApi#subtype(TargetSelector, Class)}</li>
         *   <li>{@link Settings#mapType(Class, Class)}</li>
         * </ul>
         *
         * <p>However, the methods above require specifying the subtypes
         * manually. This class allows subtype resolution to be done
         * automatically. For example, if this method maps an abstract
         * {@code Animal} class to a concrete {@code Dog} class, then
         * Instancio will generate objects based on this mapping:
         *
         * <pre>{@code
         *   Animal animal = Instancio.create(Animal.class);
         *   assertThat(animal).isExactlyInstanceOf(Dog.class);
         * }</pre>
         *
         * @param type the type to map to a subtype
         * @return the subtype class, or {@code null} if no subtype is defined
         * @throws InstancioSpiException if the returned class is not
         *                               a subtype of the argument class
         * @since 2.11.0
         */
        Class<?> getSubtype(Class<?> type);
    }

    /**
     * Provides custom instantiation logic.
     *
     * <p>An implementation of this interface can be returned
     * via the {@link #getTypeInstantiator()} method.
     *
     * @since 2.11.0
     */
    interface TypeInstantiator {

        /**
         * Instantiates an instance of the specified type.
         *
         * <p>The main use-case for implementing this interface is to provide
         * custom instantiation logic. Instancio will attempt to instantiate
         * classes using this method. If the object returned by this method
         * is {@code null}, then Instancio will attempt creating an instance
         * using internal instantiators.
         *
         * <p>Implementations of this interface are expected only to
         * instantiate, but not populate objects. By default, Instancio will
         * overwrite initialised values unless
         * {@link Keys#OVERWRITE_EXISTING_VALUES} is set to {@code false}.
         *
         * @param type the type to instantiate
         * @return an instance of the specified {@code type},
         * or {@code null} to delegate instantiation to Instancio
         * @throws InstancioSpiException if the returned object is not an
         *                               instance of the specified {@code type}
         * @since 2.11.0
         */
        Object instantiate(Class<?> type);
    }

    /**
     * Resolves setter method based on a given node when
     * {@link Keys#ASSIGNMENT_TYPE} is set to {@link AssignmentType#METHOD}.
     *
     * <p>An implementation of this interface can be returned
     * via the {@link #getSetterMethodResolver()} method.
     *
     * @since 3.2.0
     */
    @ExperimentalApi
    interface SetterMethodResolver {

        /**
         * Returns the setter method for the given {@code node}.
         * If {@code null} is returned, Instancio will attempt to resolve
         * the method using built-in resolvers based on the value of
         * {@link Keys#SETTER_STYLE}.
         *
         * @param node to resolve the setter method for
         * @return setter method or {@code null} if method was not resolved
         * @since 3.2.0
         */
        @ExperimentalApi
        Method getSetter(Node node);
    }
}
