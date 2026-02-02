/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.generator.specs;

import org.instancio.Instancio;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorSpec;

import java.util.function.Predicate;

/**
 * Generator spec for producing CSV.
 *
 * @since 2.12.0
 */
@ExperimentalApi
public interface CsvGeneratorSpec extends GeneratorSpec<String> {

    /**
     * Specifies the column name and the generator for producing the values.
     *
     * <p>Example:
     * <pre>{@code
     *   DataImport data = Instancio.of(DataImport.class)
     *       .generate(field(DataImport::getCsv), gen -> gen.text().csv()
     *           .column("productCode", random -> random.upperCaseAlphabetic(5))
     *           .column("skuCode", new SkuCodeGenerator())
     *           .column("quantity", random -> random.intRange(1, 100)))
     *      .create();
     * }</pre>
     *
     * @param name      of the column
     * @param generator for generating values
     * @return spec builder
     * @see #column(String, GeneratorSpec)
     * @since 2.12.0
     */
    CsvGeneratorSpec column(String name, Generator<?> generator);

    /**
     * Specifies the column name and the generator spec for producing the values.
     *
     * <p>This method can be used with {@link Instancio#gen()}, for example:
     *
     * <pre>{@code
     *   String csv = Instancio.gen().text().csv()
     *      .column("productCode", Instancio.gen().string().length(5))
     *      .column("quantity", Instancio.gen().ints().range(1, 100))
     *      .get();
     * }</pre>
     *
     * @param name          of the column
     * @param generatorSpec for generating values
     * @return spec builder
     * @see #column(String, Generator)
     * @since 2.12.0
     */
    CsvGeneratorSpec column(String name, GeneratorSpec<?> generatorSpec);

    /**
     * Number of rows to generate.
     *
     * @param rows number of rows to generate
     * @return spec builder
     * @see #rows(int, int)
     * @since 2.12.0
     */
    CsvGeneratorSpec rows(int rows);

    /**
     * A range for the number of rows to generate.
     * A random number of rows within the given range will be generated.
     *
     * @param minRows minimum number of rows (inclusive)
     * @param maxRows maximum number of rows (inclusive)
     * @return spec builder
     * @see #rows(int)
     * @since 2.12.0
     */
    CsvGeneratorSpec rows(int minRows, int maxRows);

    /**
     * Omit CSV header from the output.
     * The default is to include the header.
     *
     * @return spec builder
     * @since 2.12.0
     */
    CsvGeneratorSpec noHeader();

    /**
     * A string to wrap the values with, for example quotes.
     * The default is {@code null}.
     *
     * @param str a string to wrap the values with.
     * @return spec builder
     * @see #wrapIf(Predicate)
     * @since 2.12.0
     */
    CsvGeneratorSpec wrapWith(String str);

    /**
     * A condition that must be satisfied to wrap a value.
     * If {@link #wrapWith(String)} is specified,
     * the default is to wrap all values.
     *
     * <p>For example, to specify that only strings should be wrapped:
     *
     * <pre>{@code
     *   String csv = Instancio.gen().text().csv()
     *      .column("column1", Instancio.gen().string())
     *      .column("column2", Instancio.gen().ints())
     *      .wrapWith("\"")
     *      .wrapIf(value -> value instanceof String)
     *      .get()
     *
     *   // Sample output:
     *   //
     *   // column1,column2
     *   // "KJDTJZRCYY",2454
     *   // "LUOQGNQUUJ",9125
     *   // "FHRFTI",6809
     * }</pre>
     *
     * @param condition for wrapping a value
     * @return spec builder
     * @see #wrapWith(String)
     * @since 2.12.0
     */
    CsvGeneratorSpec wrapIf(Predicate<Object> condition);

    /**
     * Specifies the delimiter used to separate values.
     * The default delimiter is a comma ({@code ','}).
     *
     * @param delimiter the character used to separate values
     * @return spec builder
     * @since 5.0.0
     */
    CsvGeneratorSpec delimiter(String delimiter);

    /**
     * Specifies the line separator.
     * The default is {@link System#lineSeparator()}.
     *
     * @param lineSeparator for separating rows
     * @return spec builder
     * @since 2.12.0
     */
    CsvGeneratorSpec lineSeparator(String lineSeparator);
}
