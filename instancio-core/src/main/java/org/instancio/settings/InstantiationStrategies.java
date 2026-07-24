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
package org.instancio.settings;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.settings.InternalInstantiationStrategies;
import org.instancio.internal.util.Fail;

import java.util.EnumSet;
import java.util.List;

/**
 * An ordered collection of {@link InstantiationStrategy} values,
 * used as the value of the {@link Keys#INSTANTIATION_STRATEGIES} setting.
 *
 * <p>Instancio applies the strategies in the order they are specified, using
 * the first one that succeeds in producing an instance. A strategy that is not
 * listed is not used. For example, omitting {@link InstantiationStrategy#ALL_ARGS}
 * disables instantiation via a value-passing constructor entirely.
 *
 * <p>Instances can be created programmatically:
 *
 * <pre>{@code
 *   Settings.create().set(Keys.INSTANTIATION_STRATEGIES,
 *           InstantiationStrategies.of(ALL_ARGS, NO_ARGS, BYPASS_CONSTRUCTOR));
 * }</pre>
 *
 * <p>or, in {@code instancio.properties}, as a comma-separated list:
 *
 * <pre>{@code
 *   instantiation.strategies=ALL_ARGS,NO_ARGS,BYPASS_CONSTRUCTOR
 * }</pre>
 *
 * @see InstantiationStrategy
 * @see Keys#INSTANTIATION_STRATEGIES
 * @since 6.0.0
 */
@ExperimentalApi
public sealed interface InstantiationStrategies permits InternalInstantiationStrategies {

    /**
     * Creates an ordered list from the given strategies.
     *
     * @param strategies the strategies, in the order they should be applied;
     *                   must be non-empty and contain no duplicates
     * @return the ordered list of strategies
     * @since 6.0.0
     */
    @ExperimentalApi
    static InstantiationStrategies of(final InstantiationStrategy... strategies) {
        ApiValidator.notEmpty(strategies, "at least one instantiation strategy must be specified");
        ApiValidator.doesNotContainNull(strategies, () -> "instantiation strategy must not be null");

        final List<InstantiationStrategy> list = List.of(strategies);

        if (EnumSet.copyOf(list).size() != list.size()) {
            throw Fail.withUsageError("duplicate instantiation strategy in: %s", list);
        }
        return new InternalInstantiationStrategies(list);
    }
}
