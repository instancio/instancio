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
package org.instancio.settings;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.util.StringUtils;

/**
 * A setting that specifies what should happen if an error occurs
 * when setting a field's value.
 *
 * @see Keys#ON_SET_FIELD_ERROR
 * @since 2.1.0
 */
@ExperimentalApi
public enum OnSetFieldError {

    /**
     * Propagate the error up by throwing {@link InstancioApiException}.
     *
     * @since 2.1.0
     */
    FAIL,

    /**
     * Ignore the error and continue populating the object.
     *
     * <p><b>Warning:</b> an error caused by assigning an incompatible type
     * is considered a user error and is never ignored.
     *
     * @since 2.1.0
     */
    IGNORE;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
