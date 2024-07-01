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

import org.instancio.feed.DataFormat;
import org.instancio.internal.ApiValidator;

public final class InternalCsvDataFormat implements DataFormat {

    private final String commentPrefix;
    private final char separatorChar;
    private final boolean trimValues;

    static InternalCsvDataFormat defaults() {
        return builder().build();
    }

    private InternalCsvDataFormat(final Builder builder) {
        this.commentPrefix = builder.commentPrefix;
        this.separatorChar = builder.separatorChar;
        this.trimValues = builder.trimValues;
    }

    String getCommentPrefix() {
        return commentPrefix;
    }

    char getSeparatorChar() {
        return separatorChar;
    }

    boolean isTrimValues() {
        return trimValues;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements CsvDataFormat {
        private static final String DEFAULT_COMMENT_PREFIX = "#";
        private static final char DEFAULT_SEPARATOR_CHAR = ',';
        private static final boolean DEFAULT_TRIM_VALUES = true;

        private String commentPrefix = DEFAULT_COMMENT_PREFIX;
        private char separatorChar = DEFAULT_SEPARATOR_CHAR;
        private boolean trimValues = DEFAULT_TRIM_VALUES;

        private Builder() {
            // instantiated via builder()
        }

        @Override
        public Builder commentPrefix(final String commentPrefix) {
            this.commentPrefix = ApiValidator.notNull(commentPrefix, "'commentPrefix' must not be null");
            return this;
        }

        @Override
        public Builder separatorChar(final char separatorChar) {
            this.separatorChar = separatorChar;
            return this;
        }

        @Override
        public Builder trimValues(final boolean trimValues) {
            this.trimValues = trimValues;
            return this;
        }

        public InternalCsvDataFormat build() {
            return new InternalCsvDataFormat(this);
        }
    }
}
