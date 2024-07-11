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
import org.instancio.settings.FeedDataTrim;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A very basic CSV parser does not support quoted values
 * or escape characters.
 */
public final class CsvDataLoader implements DataLoader<List<String[]>> {

    private final FeedDataTrim feedDataTrim;
    private final String commentChar;
    private final Pattern delimiterMatcher;

    CsvDataLoader(final InternalCsvFormatOptions formatOptions) {
        this.feedDataTrim = formatOptions.getFeedDataTrim();
        this.commentChar = formatOptions.getCommentPrefix();
        this.delimiterMatcher = Pattern.compile(
                String.valueOf(formatOptions.getDelimiter()), Pattern.LITERAL);
    }

    @Override
    @SuppressWarnings("PMD.CyclomaticComplexity")
    public List<String[]> load(final DataSource dataSource) throws Exception {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(getInputStream(dataSource)))) {

            final List<String[]> results = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) { //NOPMD
                if (line.isEmpty() || line.startsWith(commentChar)) {
                    continue;
                }
                final String[] tokens = delimiterMatcher.split(line);
                for (int i = 0; i < tokens.length; i++) {
                    final String val = feedDataTrim == FeedDataTrim.NONE
                            ? tokens[i]
                            : tokens[i].trim();

                    tokens[i] = val.isEmpty() ? null : val; // NOPMD
                }
                results.add(tokens);
            }
            return results;
        }
    }
}
