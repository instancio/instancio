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
package org.instancio.settings;

import org.instancio.documentation.ExperimentalApi;
import org.instancio.internal.util.StringUtils;

/**
 * A setting that specifies whether Bean Validation annotations
 * are declared on fields or getters.
 *
 * @see Settings
 * @see Keys#BEAN_VALIDATION_ENABLED
 * @since 3.4.0
 */
@ExperimentalApi
public enum BeanValidationTarget {

    /**
     * Indicates that constraints are specified on fields (default value).
     */
    FIELD,

    /**
     * Indicates that constraints are specified on getters.
     */
    GETTER;

    @Override
    public String toString() {
        return StringUtils.enumToString(this);
    }
}
