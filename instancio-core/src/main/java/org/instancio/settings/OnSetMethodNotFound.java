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

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.StringUtils;

/**
 * A setting that specifies what should happen if a setter cannot be resolved.
 *
 * @see Keys#ON_SET_METHOD_NOT_FOUND
 * @since 2.1.0
 */
public enum OnSetMethodNotFound {

    /**
     * Use field assignment as the fallback (default behaviour).
     *
     * @since 2.1.0
     */
    ASSIGN_FIELD,

    /**
     * Fail by throwing {@link InstancioApiException}.
     *
     * <p>Note that the exception is <b>not</b> thrown if a field
     * is {@code final}, because a {@code final} field cannot have a setter.
     *
     * @since 2.1.0
     */
    FAIL,

    /**
     * Ignore the error and continue populating the object.
     *
     * @since 2.1.0
     */
    IGNORE;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
