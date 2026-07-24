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
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.StringUtils;

/**
 * A setting that specifies what should happen if an error occurs
 * when invoking a constructor that Instancio has chosen to instantiate
 * an object with (a value-passing constructor or the no-argument constructor).
 *
 * <p>This setting applies only when a constructor is actually invoked and
 * throws. It does not apply when no suitable constructor could be selected,
 * nor to records, which can only be created via constructor.
 *
 * @see Keys#ON_CONSTRUCTOR_ERROR
 * @see InstantiationStrategy
 * @since 6.0.0
 */
@ExperimentalApi
public enum OnConstructorError {

    /**
     * Bypass the failed constructor: allocate the object without invoking
     * any constructor and populate it by assigning values directly to
     * fields or via setters.
     *
     * <p>Note that this does <b>not</b> resume the
     * {@link Keys#INSTANTIATION_STRATEGIES} list at the next strategy: no
     * other constructor is attempted, only allocation without a constructor.
     * Since only {@link InstantiationStrategy#BYPASS_CONSTRUCTOR} can allocate
     * an object that way, {@code create()} returns {@code null} if that
     * strategy is not listed (or if the JVM does not permit the allocation).
     *
     * @since 6.0.0
     */
    FALLBACK,

    /**
     * Propagate the error up by throwing {@link InstancioApiException}.
     *
     * @since 6.0.0
     */
    FAIL;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
