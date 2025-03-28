/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal;

public enum ApiMethodSelector {
    APPLY_FEED("applyFeed()"),
    ASSIGN_DESTINATION("assign() destination"),
    ASSIGN_ORIGIN("assign() origin"),
    FILTER("filter()"),
    GENERATE("generate()"),
    IGNORE("ignore()"),
    /**
     * Only for internal selector (e.g. {@code setBlank()})
     * that are not reported in "unused selectors" error message.
     */
    NONE(""),
    ON_COMPLETE("onComplete()"),
    SET("set()"),
    SET_MODEL("setModel()"),
    SUBTYPE("subtype()"),
    SUPPLY("supply()"),
    WITH_NULLABLE("withNullable()"),
    WITH_UNIQUE("withUnique()");

    private final String description;

    ApiMethodSelector(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
