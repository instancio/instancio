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
package org.instancio.feed;

import org.instancio.documentation.ExperimentalApi;

/**
 * Marker interface for supported data formats.
 *
 * @since 5.0.0
 */
@ExperimentalApi
public interface DataFormat {

    /**
     * Interface for configuring CSV data format options.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    interface CsvDataFormat extends DataFormat {

        /**
         * Sets the comment prefix (lines starting with
         * this prefix will be treated as comments);
         * default value is {@code "#"}.
         *
         * @param commentPrefix the prefix for comment lines
         * @return builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        CsvDataFormat commentPrefix(String commentPrefix);

        /**
         * Sets the separator character;
         * default value is {@code ','}.
         *
         * @param separatorChar the character to use as the separator
         * @return builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        CsvDataFormat separatorChar(char separatorChar);

        /**
         * Specifies whether to trim leading and trailing whitespace from values;
         * default value is {@code true}.
         *
         * @param shouldTrimValues {@code true} to trim values, {@code false} otherwise
         * @return builder reference
         * @since 5.0.0
         */
        @ExperimentalApi
        CsvDataFormat trimValues(boolean shouldTrimValues);
    }
}
