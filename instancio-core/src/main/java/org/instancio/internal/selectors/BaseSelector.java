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
package org.instancio.internal.selectors;

import org.instancio.Scope;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

abstract class BaseSelector implements UnusedSelectorDescription, InternalSelector {

    private final ApiMethodSelector apiMethodSelector;
    private final List<Scope> scopes;
    private final Throwable stackTraceHolder;
    private final boolean isLenient;
    private final boolean isHiddenFromVerboseOutput;

    BaseSelector(
            final ApiMethodSelector apiMethodSelector,
            final List<Scope> scopes,
            final Throwable stackTraceHolder,
            final boolean isLenient,
            final boolean isHiddenFromVerboseOutput) {

        this.apiMethodSelector = apiMethodSelector;
        this.scopes = Collections.unmodifiableList(scopes);
        this.stackTraceHolder = stackTraceHolder;
        this.isLenient = isLenient;
        this.isHiddenFromVerboseOutput = isHiddenFromVerboseOutput;
    }

    @Override
    public ApiMethodSelector getApiMethodSelector() {
        return apiMethodSelector;
    }

    @NotNull
    @Override
    public final List<Scope> getScopes() {
        return scopes;
    }

    @Override
    public final List<TargetSelector> flatten() {
        return Collections.singletonList(this);
    }

    @Override
    public final boolean isLenient() {
        return isLenient;
    }

    @Override
    public final boolean isHiddenFromVerboseOutput() {
        return isHiddenFromVerboseOutput;
    }

    @Override
    public final String getDescription() {
        return String.format("%s%n    at %s", this, Format.firstNonInstancioStackTraceLine(getStackTraceHolder()));
    }

    public final Throwable getStackTraceHolder() {
        return stackTraceHolder;
    }
}
