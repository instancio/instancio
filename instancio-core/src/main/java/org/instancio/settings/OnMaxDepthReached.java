/*
 * Copyright 2022-2025 the original author or authors.
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
 * Specifies how to handle objects when the configured maximum depth is reached.
 *
 * @see Keys#MAX_DEPTH
 * @see Keys#ON_MAX_DEPTH_REACHED
 * @since 6.0.0
 */
@ExperimentalApi
public enum OnMaxDepthReached {

    /**
     * Fail when the maximum depth is reached.
     *
     * <p>Throws an error immediately when the maximum depth is reached,
     * regardless of whether additional nodes remain to be processed.
     *
     * @since 6.0.0
     */
    FAIL,

    /**
     * Ignore and continue without creating deeper objects.
     *
     * @since 6.0.0
     */
    IGNORE;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
