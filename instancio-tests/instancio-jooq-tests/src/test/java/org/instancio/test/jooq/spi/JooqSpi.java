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
package org.instancio.test.jooq.spi;

import org.instancio.Node;
import org.instancio.settings.Keys;
import org.instancio.spi.InstancioServiceProvider;
import org.instancio.spi.ServiceProviderContext;
import org.jooq.Field;
import org.jooq.Record;

public class JooqSpi implements InstancioServiceProvider {

    private int stringSettingsMaxLength;

    @Override
    public void init(final ServiceProviderContext context) {
        stringSettingsMaxLength = context.getSettings().get(Keys.STRING_MAX_LENGTH);
    }

    /**
     * This SPI implementation sets max length of generated
     * strings based on column length.
     */
    @Override
    public GeneratorProvider getGeneratorProvider() {
        return (node, generators) -> {
            // set max length of generated strings
            if (node.getTargetClass() != String.class
                    || !Record.class.isAssignableFrom(node.getParent().getTargetClass())) {
                return null;
            }

            final int columnSize = getColumnSize(node);
            final int maxLength = Math.min(columnSize, stringSettingsMaxLength);
            return generators.string().maxLength(maxLength);
        };
    }

    /**
     * Returns column size for this node based no jOOQ metadata.
     *
     * <p>Generating values that fit within column size would be useful for
     * integration tests (e.g. persisting objects), but less so for unit tests.
     */
    private static int getColumnSize(final Node node) {
        try {
            // A bit of a hack to get column size.
            // Is it possible to do this without instantiating the record?
            final Record rec = (Record) node.getParent().getTargetClass().getDeclaredConstructor().newInstance();
            final String columnName = getColumnName(node.getSetter().getName());
            final Field<?> field = rec.field(columnName);
            return field.getDataType().length();
        } catch (Exception ex) {
            throw new AssertionError(ex);
        }

    }

    private static String getColumnName(final String setterName) {
        final StringBuilder columnName = new StringBuilder();

        // skip "set" prefix
        for (int i = 3; i < setterName.length(); i++) {
            final char c = setterName.charAt(i);

            if (i == 3) {
                columnName.append(c);
            } else if (Character.isUpperCase(c)) {
                columnName.append("_").append(c);
            } else {
                columnName.append(Character.toUpperCase(c));
            }
        }

        return columnName.toString();
    }
}
