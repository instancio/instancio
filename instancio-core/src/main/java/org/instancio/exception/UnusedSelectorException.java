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
package org.instancio.exception;

import org.instancio.TargetSelector;
import org.instancio.internal.util.Sonar;

import java.util.Set;

/**
 * Exception triggered as a result of unused selectors.
 *
 * @since 1.3.3
 */
@SuppressWarnings({Sonar.NUMBER_OF_PARENTS, Sonar.GENERIC_WILDCARD_IN_RETURN})
public class UnusedSelectorException extends InstancioApiException {

    private final transient Set<? super TargetSelector> ignored;
    private final transient Set<? super TargetSelector> nullable;
    private final transient Set<? super TargetSelector> generators;
    private final transient Set<? super TargetSelector> callbacks;
    private final transient Set<? super TargetSelector> subtypes;

    public UnusedSelectorException(
            final String message,
            final Set<? super TargetSelector> ignored,
            final Set<? super TargetSelector> nullable,
            final Set<? super TargetSelector> generators,
            final Set<? super TargetSelector> callbacks,
            final Set<? super TargetSelector> subtypes) {
        super(message);
        this.ignored = ignored;
        this.nullable = nullable;
        this.generators = generators;
        this.callbacks = callbacks;
        this.subtypes = subtypes;
    }

    public Set<? super TargetSelector> getIgnored() {
        return ignored;
    }

    public Set<? super TargetSelector> getNullable() {
        return nullable;
    }

    public Set<? super TargetSelector> getGenerators() {
        return generators;
    }

    public Set<? super TargetSelector> getCallbacks() {
        return callbacks;
    }

    public Set<? super TargetSelector> getSubtypes() {
        return subtypes;
    }
}
