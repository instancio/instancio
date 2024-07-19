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
package org.instancio.exception;

import org.instancio.TargetSelector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.internal.util.Sonar;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Exception triggered as a result of unused selectors.
 *
 * @since 1.3.3
 */
@SuppressWarnings({Sonar.NUMBER_OF_PARENTS, Sonar.GENERIC_WILDCARD_IN_RETURN})
public class UnusedSelectorException extends InstancioApiException {

    private final transient Map<ApiMethodSelector, List<TargetSelector>> unusedSelectorMap;

    public UnusedSelectorException(
            final String message,
            final Map<ApiMethodSelector, List<TargetSelector>> unusedSelectorMap) {

        super(message);
        this.unusedSelectorMap = Collections.unmodifiableMap(unusedSelectorMap);
    }

    public Map<ApiMethodSelector, List<TargetSelector>> getUnusedSelectorMap() {
        return unusedSelectorMap;
    }
}
