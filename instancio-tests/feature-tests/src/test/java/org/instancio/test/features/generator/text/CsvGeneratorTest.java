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

package org.instancio.test.features.generator.text;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATE, Feature.CSV_GENERATOR})
@ExtendWith(InstancioExtension.class)
class CsvGeneratorTest {

    @Test
    void csv1Col() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1)
                        .column("col1", random -> "val1"))
                .create();

        assertThat(result).isEqualTo(String.format("col1%nval1"));
    }

    @Test
    void csv1ColZeroRows() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(0)
                        .column("col1", random -> "val1"))
                .create();

        assertThat(result).isEqualTo(String.format("col1%n"));
    }

    @Test
    void csv1ColZeroRowsNoHeader() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(0)
                        .noHeader()
                        .column("col1", random -> "val1"))
                .create();

        assertThat(result).isEmpty();
    }

    @Test
    void csv1ColNoHeader() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1)
                        .noHeader()
                        .column("col1", random -> "val1"))
                .create();

        assertThat(result).isEqualTo("val1");
    }

    @Test
    void csv2Cols() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1)
                        .column("col1", random -> "val1")
                        .column("col2", random -> "val2"))
                .create();

        assertThat(result).isEqualTo(String.format("col1,col2%nval1,val2"));
    }

    @Test
    void csv2ColsSeparator() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1)
                        .delimiter(" || ")
                        .column("col1", random -> "val1")
                        .column("col2", random -> "val2"))
                .create();

        assertThat(result).isEqualTo(String.format("col1 || col2%nval1 || val2"));
    }

    @Test
    void csv2ColsLineSeparator() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1)
                        .lineSeparator(" --- ")
                        .column("col1", random -> "val1")
                        .column("col2", random -> "val2"))
                .create();

        assertThat(result).isEqualTo("col1,col2 --- val1,val2");
    }

    @Test
    void csv2ColsWrapWith() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1)
                        .wrapWith("\"")
                        .column("col1", random -> "val1")
                        .column("col2", random -> "val2"))
                .create();

        assertThat(result).isEqualTo(String.format("col1,col2%n\"val1\",\"val2\""));
    }

    @Test
    void csv2ColsWrapWithCondition() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1)
                        .wrapWith("\"")
                        .wrapIf(obj -> obj instanceof String)
                        .column("col1", random -> "val1")
                        .column("col2", random -> 123))
                .create();

        assertThat(result).isEqualTo(String.format("col1,col2%n\"val1\",123"));
    }

    @Test
    void csvNoColumnsSpecified() {
        final InstancioApi<String> api = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv());

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("at least 1 column is required to generate CSV");
    }

    @Test
    void csv1RowRange() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().csv()
                        .rows(1, 2)
                        .noHeader()
                        .column("col1", random -> "val1"))
                .create();

        assertThat(result).matches(r ->
                r.equals("val1") || r.equals(String.format("val1%nval1")));
    }
}