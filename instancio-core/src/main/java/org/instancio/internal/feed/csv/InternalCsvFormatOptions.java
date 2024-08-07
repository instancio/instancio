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

import org.instancio.feed.FormatOptionsProvider;
import org.instancio.internal.ApiValidator;
import org.instancio.settings.FeedDataTrim;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import static java.lang.String.format;

public final class InternalCsvFormatOptions implements FormatOptionsProvider.FormatOptions {

    private final String commentPrefix;
    private final char delimiter;
    private final FeedDataTrim feedDataTrim;

    static InternalCsvFormatOptions defaults(final Settings settings) {
        return builder().build(settings);
    }

    private InternalCsvFormatOptions(final Builder builder, final Settings settings) {
        this.commentPrefix = builder.commentPrefix;
        this.delimiter = validateDelimiter(builder.delimiter);
        this.feedDataTrim = builder.feedDataTrim != null
                ? builder.feedDataTrim
                : settings.get(Keys.FEED_DATA_TRIM);
    }

    private char validateDelimiter(char delimiter) {
        if (delimiter == '"') {
            throw new IllegalArgumentException(format("Invalid delimiter: %s", delimiter));
        }
        return delimiter;
    }

    String getCommentPrefix() {
        return commentPrefix;
    }

    char getDelimiter() {
        return delimiter;
    }

    FeedDataTrim getFeedDataTrim() {
        return feedDataTrim;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements CsvFormatOptions {
        private static final String DEFAULT_COMMENT_PREFIX = "#";
        private static final char DEFAULT_DELIMITER = ',';

        private String commentPrefix = DEFAULT_COMMENT_PREFIX;
        private char delimiter = DEFAULT_DELIMITER;
        private FeedDataTrim feedDataTrim;

        private Builder() {
            // instantiated via builder()
        }

        @Override
        public Builder commentPrefix(final String commentPrefix) {
            this.commentPrefix = ApiValidator.notNull(commentPrefix, "'commentPrefix' must not be null");
            return this;
        }

        @Override
        public Builder delimiter(final char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        @Override
        public Builder trim(final FeedDataTrim feedDataTrim) {
            this.feedDataTrim = feedDataTrim;
            return this;
        }

        public InternalCsvFormatOptions build(final Settings settings) {
            return new InternalCsvFormatOptions(this, settings);
        }
    }
}
