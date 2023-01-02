/*
 * Copyright 2022-2023 the original author or authors.
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

import java.util.Optional;

/**
 * A Service Provider Interface for mapping types to subtypes.
 *
 * <p>Implementations of this class can be registered by placing a file named
 * {@code org.instancio.spi.TypeResolver} under {@code /META-INF/services}.
 * The file must contain the fully-qualified name of the implementing class.
 * </p>
 *
 * <p>The primary use case for this class is to map types to subtypes
 * dynamically at runtime. Similar functionality can be achieved
 * using {@link org.instancio.settings.Settings} as follows:
 * </p>
 *
 * <pre>{@code
 *   Settings settings = Settings.create()
 *       .mapType(AbstractWidget.class, ConcreteWidget.class);
 *
 *   AbstractWidget widget = Instancio.of(AbstractWidget.class)
 *       .withSettings(settings)
 *       .create();
 *
 *   assertThat(widget).isExactlyInstanceOf(ConcreteWidget.class);
 * }</pre>
 *
 * <p>However, the above approach requires passing in the {@code Settings} as an argument,
 * or using settings injection via the {@code @WithSettings} annotation and
 * {@code InstancioExtension} for JUnit Jupiter.
 * </p>
 *
 * <p>In situations where the above is too cumbersome, custom {@link TypeResolver}
 * can be registered to resolves subtypes. Once registered, the resolution
 * will be done automatically, for example:
 * </p>
 *
 * <pre>{@code
 *   AbstractWidget widget = Instancio.create(AbstractWidget.class);
 *   assertThat(widget).isExactlyInstanceOf(ConcreteWidget.class);
 * }</pre>
 *
 * <p>Using the SPI also allows providing resolver implementations that perform
 * resolution by scanning the classpath.
 * </p>
 *
 * @since 1.6.0
 */
public interface TypeResolver {

    /**
     * Resolves a subtype for a given type.
     *
     * @param type to resolve
     * @return an {@link Optional} containing a subtype, or an empty result if unresolved.
     * @since 1.6.0
     */
    Optional<Class<?>> resolve(Class<?> type);

}
