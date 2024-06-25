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
package org.instancio.internal.schema.csv;

import org.instancio.internal.schema.DataLoader;
import org.instancio.internal.schema.datasource.CacheableDataSource;
import org.instancio.internal.util.Fail;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A very basic CSV parser that does not support quoted values.
 */
public final class CsvDataLoader implements DataLoader<String[]> {

    private static final String COMMENT_CHAR = "#";
    private static final String SEPARATOR_CHAR = ",";
    private static final boolean TRIM_VALUES = true;

    private final String commentChar;
    private final String separatorChar;
    private final boolean trimValues;

    public CsvDataLoader() {
        this(COMMENT_CHAR, SEPARATOR_CHAR, TRIM_VALUES);
    }

    public CsvDataLoader(
            final String commentChar,
            final String separatorChar,
            final boolean trimValues) {

        this.commentChar = commentChar;
        this.separatorChar = separatorChar;
        this.trimValues = trimValues;
    }

    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public List<String[]> load(final CacheableDataSource dataSource) {
        final InputStream in = dataSource.getInputStream();
        if (in == null) {
            String msg = "failed loading data due to a null InputStream";
            if (dataSource.getName() != null) {
                msg += String.format(" (source: %s)", dataSource.getName());
            }
            throw Fail.withUsageError(msg);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            final List<String[]> results = new ArrayList<>();

            String line;
            while ((line = reader.readLine()) != null) { //NOPMD
                if (line.isEmpty() || line.startsWith(commentChar)) {
                    continue;
                }
                final String[] tokens = line.split(separatorChar);
                if (tokens.length == 0) {
                    continue;
                }
                for (int i = 0; i < tokens.length; i++) {
                    tokens[i] = trimValues ? tokens[i].trim() : tokens[i];
                }
                results.add(tokens);
            }
            return results;
        } catch (Exception ex) {
            throw Fail.withInternalError("Error loading input stream", ex);
        }
    }
}
