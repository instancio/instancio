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
import org.instancio.internal.feed.csv.InternalCsvDataFormat;

/**
 * Provides a {@link DataFormat} using the specified {@link DataFormatFactory}.
 *
 * @since 5.0.0
 */
@ExperimentalApi
@FunctionalInterface
public interface DataFormatProvider {

    /**
     * Gets a {@link DataFormat} from the given {@code factory}.
     *
     * @param factory the factory for creating the data format
     * @return the created data source
     * @since 5.0.0
     */
    @ExperimentalApi
    DataFormat get(DataFormatFactory factory);

    /**
     * Creates {@link DataFormat} objects for support formats
     * (currently, only CSV is supported).
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    interface DataFormatFactory {

        /**
         * Allows specifying configuration options for parsing CSV files.
         *
         * @return CSV format builder
         * @since 5.0.0
         */
        @ExperimentalApi
        default DataFormat.CsvDataFormat csv() {
            return InternalCsvDataFormat.builder();
        }
    }
}
