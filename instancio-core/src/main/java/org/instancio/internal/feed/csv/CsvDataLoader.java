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
package org.instancio.internal.feed.csv;

import org.instancio.feed.DataSource;
import org.instancio.internal.feed.DataLoader;
import org.instancio.internal.util.Fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A very basic CSV parser does not support quoted values
 * or escape characters.
 */
public final class CsvDataLoader implements DataLoader<String[]> {

    private final boolean trimValues;
    private final String commentChar;
    private final Pattern separatorMatcher;

    CsvDataLoader(final InternalCsvDataFormat dataFormat) {
        this.trimValues = dataFormat.isTrimValues();
        this.commentChar = dataFormat.getCommentPrefix();
        this.separatorMatcher = Pattern.compile(
                String.valueOf(dataFormat.getSeparatorChar()), Pattern.LITERAL);
    }

    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public List<String[]> load(final DataSource dataSource) {
        try (BufferedReader br = new BufferedReader(getInputStreamReader(dataSource))) {
            final List<String[]> results = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) { //NOPMD
                if (line.isEmpty() || line.startsWith(commentChar)) {
                    continue;
                }
                final String[] tokens = separatorMatcher.split(line);
                if (tokens.length == 0) {
                    continue;
                }
                for (int i = 0; i < tokens.length; i++) {
                    final String val = trimValues ? tokens[i].trim() : tokens[i];
                    tokens[i] = val.isEmpty() ? null : val; // NOPMD
                }
                results.add(tokens);
            }
            return results;
        } catch (Exception ex) {
            throw Fail.withUsageError(ioErrorMessage(dataSource), ex);
        }
    }

    private static String ioErrorMessage(final DataSource dataSource) {
        String msg = "failed loading data due to a null InputStream";
        if (dataSource.getName() != null) {
            msg += String.format(" (source: %s)", dataSource.getName());
        }
        return msg;
    }

    private static InputStreamReader getInputStreamReader(final DataSource dataSource) {
        try {
            return new InputStreamReader(dataSource.getInputStream());
        } catch (IOException ex) {
            throw Fail.withUsageError(ioErrorMessage(dataSource), ex);
        }
    }
}
