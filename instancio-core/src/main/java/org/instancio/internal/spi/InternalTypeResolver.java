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

import org.instancio.documentation.InternalApi;
import org.instancio.spi.InstancioServiceProvider;

/**
 * Internal extension of {@link InstancioServiceProvider.TypeResolver}
 * with additional configuration options.
 *
 * @since 6.0.0
 */
@InternalApi
public interface InternalTypeResolver extends InstancioServiceProvider.TypeResolver {

    /**
     * Returns {@code false} to disable subtype validation during node graph construction.
     *
     * <p>Intended for implementations that remap types in ways that would
     * not pass standard subtype checks (e.g. remapping primitives or enums).
     *
     * @return {@code true} to enable subtype validation; {@code false} to disable it.
     * @since 6.0.0
     */
    boolean isSubtypeValidationEnabled();

}