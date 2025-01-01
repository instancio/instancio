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
package org.instancio.internal.feed.csv;

import org.instancio.internal.feed.AbstractFeed;
import org.instancio.internal.feed.DataStore;
import org.instancio.internal.feed.InternalFeedContext;

class CsvFeed extends AbstractFeed<String[]> {

    CsvFeed(
            final InternalFeedContext<?> feedContext,
            final DataStore<String[]> dataStore) {

        super(feedContext, dataStore);
    }

    @Override
    protected String getValue(final String propertyKey) {
        final int index = getPropertyIndex(propertyKey);
        final String[] currentEntry = getCurrentEntry();

        // Handle cases where a CSV record does not have values for all the columns
        if (index < currentEntry.length) {
            return currentEntry[index];
        }
        return null;
    }

}
