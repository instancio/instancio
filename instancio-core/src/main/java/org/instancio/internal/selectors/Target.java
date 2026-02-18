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
package org.instancio.internal.selectors;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.spi.InternalServiceProvider;
import org.instancio.internal.util.Fail;
import org.jspecify.annotations.Nullable;

import java.util.List;

/**
 * Represents the target of a {@link org.instancio.Selector}
 * or {@link org.instancio.Scope}.
 *
 * @since 4.0.0
 */
@InternalApi
public sealed interface Target permits
        TargetClass,
        TargetField,
        TargetFieldName,
        TargetGetterReference,
        TargetRoot,
        TargetSetter,
        TargetSetterName,
        TargetSetterReference {

    @Nullable Class<?> getTargetClass();

    default ScopelessSelector toScopelessSelector() {
        throw Fail.withFataInternalError("Unhandled selector target: %s", getTargetClass());
    }

    /**
     * Creates a new {@link Target} by associating the root class from
     * the given {@link TargetContext} with this instance, if applicable.
     *
     * <p>If this instance already contains the root class,
     * the current instance is returned unchanged.
     *
     * @param targetContext the context containing the root class to apply
     * @return a new {@code Target} instance with the root class applied, or the current instance
     * if no changes are necessary
     */
    default Target withRootClass(TargetContext targetContext) {
        return this;
    }

    final class TargetContext {

        private final Class<?> rootClass;
        private final List<InternalServiceProvider> internalServiceProviders;

        TargetContext(final Class<?> rootClass, final List<InternalServiceProvider> internalServiceProviders) {
            this.rootClass = rootClass;
            this.internalServiceProviders = internalServiceProviders;
        }

        Class<?> getRootClass() {
            return rootClass;
        }

        List<InternalServiceProvider> getInternalServiceProviders() {
            return internalServiceProviders;
        }
    }
}
