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
package org.instancio.internal.settings;

import org.instancio.internal.ApiValidator;
import org.instancio.internal.util.Fail;
import org.instancio.settings.InstantiationStrategies;
import org.instancio.settings.InstantiationStrategy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

public final class InternalInstantiationStrategies implements InstantiationStrategies {

    private final List<InstantiationStrategy> instantiationStrategies;

    public InternalInstantiationStrategies(final List<InstantiationStrategy> strategies) {
        this.instantiationStrategies = Collections.unmodifiableList(strategies);
    }

    public static InstantiationStrategies parse(final String csvValues) {
        ApiValidator.isFalse(csvValues.isBlank(), "at least one instantiation strategy must be specified");

        final String[] tokens = csvValues.split(",");
        final InstantiationStrategy[] parsed = new InstantiationStrategy[tokens.length];

        for (int i = 0; i < tokens.length; i++) {
            final String token = tokens[i].trim().toUpperCase(Locale.ROOT);

            try {
                parsed[i] = InstantiationStrategy.valueOf(token);
            } catch (IllegalArgumentException ex) {
                throw Fail.withUsageError("invalid instantiation strategy '%s' in: %s%n"
                                + "Valid values are: %s",
                        tokens[i].trim(), csvValues, Arrays.toString(InstantiationStrategy.values()), ex);
            }
        }
        return InstantiationStrategies.of(parsed);
    }

    public List<InstantiationStrategy> getStrategies() {
        return instantiationStrategies;
    }

    public boolean contains(final InstantiationStrategy strategy) {
        return instantiationStrategies.contains(strategy);
    }

    @Override
    @SuppressWarnings("PMD.SimplifyBooleanReturns")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        return o instanceof InternalInstantiationStrategies is
                && instantiationStrategies.equals(is.instantiationStrategies);
    }

    @Override
    public int hashCode() {
        return instantiationStrategies.hashCode();
    }

    /**
     * Returns the strategies as a comma-separated list of names,
     * matching the format accepted by {@link #parse(String)}.
     */
    @Override
    public String toString() {
        final StringJoiner joiner = new StringJoiner(",");
        for (InstantiationStrategy strategy : instantiationStrategies) {
            joiner.add(strategy.name());
        }
        return joiner.toString();
    }
}
