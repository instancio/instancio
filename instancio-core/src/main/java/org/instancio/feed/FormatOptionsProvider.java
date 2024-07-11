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
import org.instancio.internal.feed.csv.InternalCsvFormatOptions;
import org.instancio.settings.FeedDataTrim;
import org.instancio.settings.Keys;

/**
 * Provides a {@link FormatOptions} using the specified {@link FormatOptionsFactory}.
 *
 * @since 5.0.0
 */
@ExperimentalApi
@FunctionalInterface
public interface FormatOptionsProvider {

    /**
     * Configures format options using the given {@code factory}.
     *
     * @param factory the factory for creating the data format
     * @return the created data source
     * @since 5.0.0
     */
    @ExperimentalApi
    FormatOptions get(FormatOptionsFactory factory);

    /**
     * Marker interface for supported data formats.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    interface FormatOptions {

        /**
         * Interface for configuring CSV data format options.
         *
         * @since 5.0.0
         */
        @ExperimentalApi
        interface CsvFormatOptions extends FormatOptions {

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
            CsvFormatOptions commentPrefix(String commentPrefix);

            /**
             * Sets the delimiter character;
             * default value is {@code ','}.
             *
             * @param delimiter the character to use as the delimiter
             * @return builder reference
             * @since 5.0.0
             */
            @ExperimentalApi
            CsvFormatOptions delimiter(char delimiter);

            /**
             * Specifies whether values should be trimmed of whitespace.
             *
             * <p>The default behaviour is determined by the
             * {@link Keys#FEED_DATA_TRIM} setting.
             *
             * @param feedDataTrim the data trimming mode
             * @return builder reference
             * @since 5.0.0
             */
            @ExperimentalApi
            CsvFormatOptions trim(FeedDataTrim feedDataTrim);
        }
    }

    /**
     * Creates {@link FormatOptions} objects for supported data format types.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    interface FormatOptionsFactory {

        /**
         * Allows specifying configuration options for parsing CSV files.
         *
         * @return CSV format builder
         * @since 5.0.0
         */
        @ExperimentalApi
        default FormatOptions.CsvFormatOptions csv() {
            return InternalCsvFormatOptions.builder();
        }
    }

}
