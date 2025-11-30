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

import org.instancio.InstancioApi;
import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.documentation.InternalApi;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.Flattener;
import org.jspecify.annotations.NonNull;

import java.util.List;

/**
 * Internal selector class that exposes common methods
 * between {@link SelectorImpl} and {@link PredicateSelectorImpl}.
 */
@InternalApi
public interface InternalSelector extends ScopeableSelector, Flattener<TargetSelector> {

    ApiMethodSelector getApiMethodSelector();

    @NonNull
    List<Scope> getScopes();

    boolean isLenient();

    /**
     * Specifies whether this selector should be reported in
     * the output produced by {@link InstancioApi#verbose()}.
     */
    boolean isHiddenFromVerboseOutput();

    /**
     * Returns {@code true} if this selector is the root
     * selector, that is {@link Select#root()}.
     */
    default boolean isRootSelector() {
        return false;
    }
}
