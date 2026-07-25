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
import org.instancio.internal.util.StringUtils;

/**
 * A strategy for instantiating an object. An ordered list of strategies
 * is configured via {@link Keys#INSTANTIATION_STRATEGIES}; Instancio applies
 * them in order, using the first one that succeeds in producing an instance.
 *
 * <p>The strategies fall into two groups. {@link #ALL_ARGS} invokes a
 * constructor with generated values passed as arguments. The remaining
 * strategies produce an instance without passing generated values:
 * {@link #NO_ARGS} invokes the no-argument constructor, while
 * {@link #BYPASS_CONSTRUCTOR} allocates an instance without invoking
 * any constructor at all.
 *
 * <p>Matching constructor parameters to fields (for {@link #ALL_ARGS})
 * requires the parameter names to be present in the class file. Maven and
 * Gradle both emit them by default (by compiling with debug information), as
 * does the {@code -parameters} compiler option. If the names are unavailable,
 * no parameters can be matched. Records are exempt: they are always created
 * via the canonical constructor, matched by position, regardless of the
 * configured strategies.
 *
 * @see InstantiationStrategies
 * @see Keys#INSTANTIATION_STRATEGIES
 * @see Keys#ON_CONSTRUCTOR_ERROR
 * @since 6.0.0
 */
@ExperimentalApi
public enum InstantiationStrategy {

    /**
     * Instantiate via constructor, but only if <b>all</b> of the constructor's
     * parameters can be unambiguously matched to fields. Generated values are
     * passed as constructor arguments.
     *
     * <p>A constructor with a parameter that matches no field is not used,
     * since the value of such a parameter could not be reached by a selector.
     * For example:
     *
     * <pre>{@code
     * class Person {
     *     String name;
     *
     *     Person(String title, String name) { // 'title' matches no field
     *         this.name = title + " " + name;
     *     }
     * }
     * }</pre>
     *
     * <p>The {@code name} parameter matches the {@code name} field, but
     * {@code title} matches none, so this constructor is skipped and the next
     * configured strategy is applied.
     *
     * @since 6.0.0
     */
    ALL_ARGS,

    /**
     * Instantiate via the no-argument constructor. No generated values are
     * passed to the constructor; the object is populated afterwards by
     * assigning values directly to fields or via setters.
     *
     * @since 6.0.0
     */
    NO_ARGS,

    /**
     * Allocate an instance without invoking any constructor (where the JVM
     * permits it). Constructor bodies and field initializers do not run;
     * the object is populated afterwards by assigning values directly to
     * fields or via setters.
     *
     * @since 6.0.0
     */
    BYPASS_CONSTRUCTOR;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
