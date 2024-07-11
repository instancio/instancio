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
import org.instancio.internal.feed.datasource.FileDataSource;
import org.instancio.internal.feed.datasource.ResourceDataSource;
import org.instancio.internal.feed.datasource.StringDataSource;

import java.io.InputStream;
import java.nio.file.Path;

/**
 * Provides a {@link DataSource} using the specified {@link DataSourceFactory}.
 *
 * @since 5.0.0
 */
@ExperimentalApi
@FunctionalInterface
public interface DataSourceProvider {

    /**
     * Gets a {@link DataSource} from the given {@code factory}.
     *
     * @param factory the factory for creating the data source
     * @return the created data source
     * @since 5.0.0
     */
    @ExperimentalApi
    DataSource get(DataSourceFactory factory);

    /**
     * Creates {@link DataSource} objects from various underlying sources.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    interface DataSourceFactory {

        /**
         * Creates a data source for the given {@code inputStream}.
         *
         * @param inputStream the input stream of the data file
         * @return a data source for the given input stream
         * @since 5.0.0
         */
        @ExperimentalApi
        default DataSource ofInputStream(final InputStream inputStream) {
            return () -> inputStream;
        }

        /**
         * Creates a data source for the given {@code path}.
         *
         * @param path the path of the data file
         * @return a data source for the given path
         * @since 5.0.0
         */
        @ExperimentalApi
        default DataSource ofFile(final Path path) {
            return new FileDataSource(path);
        }

        /**
         * Creates a data source for the given resource {@code name}.
         *
         * @param name the name of the resource containing the data.
         * @return a data source for the given resource name
         * @since 5.0.0
         */
        @ExperimentalApi
        default DataSource ofResource(final String name) {
            return new ResourceDataSource(name);
        }

        /**
         * Creates a data source from the given {@code data} string.
         *
         * @param data the data represented as a string
         * @return a data source for the data string
         * @since 5.0.0
         */
        @ExperimentalApi
        default DataSource ofString(final String data) {
            return new StringDataSource(data);
        }
    }
}
