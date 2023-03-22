/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal.generator.text;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.GeneratorSpec;
import org.instancio.generator.specs.CsvSpec;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.generator.AbstractGenerator;
import org.instancio.support.Global;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class CsvGenerator extends AbstractGenerator<String> implements CsvSpec {

    private int minRows = 1;
    private int maxRows = 10;
    private boolean includeHeader = true;
    private String wrapWith;
    private Predicate<Object> wrapIf = o -> true;
    private String separator = ",";
    private String lineSeparator = System.lineSeparator();
    private final List<Column> columns = new ArrayList<>();

    private static final class Column {
        private final String name;
        private final GeneratorSpec<?> generator;

        private Column(final String name, final GeneratorSpec<?> generator) {
            this.name = name;
            this.generator = generator;
        }
    }

    public CsvGenerator() {
        this(Global.generatorContext());
    }

    public CsvGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "csv()";
    }

    @Override
    public CsvGenerator column(final String name, final GeneratorSpec<?> generatorSpec) {
        return column(name, (Generator<?>) generatorSpec);
    }

    @Override
    public CsvGenerator column(final String name, final Generator<?> generator) {
        columns.add(new Column(
                ApiValidator.notNull(name, "column() name must not be null"),
                ApiValidator.notNull(generator, "column() generator must not be null")));
        return this;
    }

    @Override
    public CsvGenerator rows(final int rows) {
        return this.rows(rows, rows);
    }

    @Override
    public CsvGenerator rows(final int min, final int max) {
        ApiValidator.isTrue(min >= 0, "Min must not be negative: " + min);
        ApiValidator.isTrue(min <= max, "Min must be less than or equal to max: (%s, %s)", min, max);
        this.minRows = min;
        this.maxRows = max;
        return this;
    }

    @Override
    public CsvGenerator noHeader() {
        includeHeader = false;
        return this;
    }

    @Override
    public CsvGenerator wrapWith(final String wrapWith) {
        this.wrapWith = wrapWith;
        return this;
    }

    @Override
    public CsvGenerator wrapIf(final Predicate<Object> wrapIf) {
        this.wrapIf = ApiValidator.notNull(wrapIf, "wrapIf() predicate must not be null");
        return this;
    }

    @Override
    public CsvGenerator separator(final String separator) {
        this.separator = separator;
        return this;
    }

    @Override
    public CsvGenerator lineSeparator(final String lineSeparator) {
        this.lineSeparator = lineSeparator;
        return this;
    }

    @Override
    public CsvGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        ApiValidator.isTrue(!columns.isEmpty(), "At least 1 column is required to generate CSV");
        final int rows = random.intRange(minRows, maxRows);
        final int initialSize = rows * columns.size() * 32;
        final StringBuilder sb = new StringBuilder(initialSize);
        final int cols = columns.size();

        if (includeHeader) {
            appendHeader(sb, cols);
        }
        for (int r = 0; r < rows; r++) {
            appendRow(sb, cols, random);
            if (r < rows - 1) {
                sb.append(lineSeparator);
            }
        }
        return sb.toString();
    }

    private void appendRow(final StringBuilder sb, final int cols, final Random random) {
        for (int c = 0; c < cols; c++) {
            final Generator<?> generator = (Generator<?>) columns.get(c).generator;
            final Object value = generator.generate(random);
            final boolean wrap = wrapWith != null && wrapIf.test(value);

            if (wrap) {
                sb.append(wrapWith);
            }
            sb.append(value);
            if (wrap) {
                sb.append(wrapWith);
            }
            if (c < cols - 1) {
                sb.append(separator);
            }
        }
    }

    private void appendHeader(final StringBuilder sb, final int cols) {
        for (int c = 0; c < cols; c++) {
            sb.append(columns.get(c).name);
            if (c < cols - 1) {
                sb.append(separator);
            }
        }
        sb.append(lineSeparator);
    }
}
