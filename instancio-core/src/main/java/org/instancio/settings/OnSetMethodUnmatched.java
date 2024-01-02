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

import org.instancio.internal.util.StringUtils;

/**
 * A setting that specifies what should happen if a setter
 * has no matching field.
 *
 * <p>See <a href="https://www.instancio.org/user-guide/#unmatched-setters">Unmatched Setters</a>
 * section of the User Guide for details.
 *
 * @see Keys#ON_SET_METHOD_UNMATCHED
 * @since 4.0.0
 */
public enum OnSetMethodUnmatched {

    /**
     * Invoke setters that do not have a corresponding field.
     *
     * @since 4.0.0
     */
    INVOKE,

    /**
     * Ignore setters that do not have a corresponding field.
     *
     * @since 4.0.0
     */
    IGNORE;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
