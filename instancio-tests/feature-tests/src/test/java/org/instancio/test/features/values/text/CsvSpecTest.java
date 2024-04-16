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
package org.instancio.test.features.values.text;

import org.instancio.Gen;
import org.instancio.generator.specs.CsvSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class CsvSpecTest extends AbstractValueSpecTestTemplate<String> {

    @Override
    protected CsvSpec spec() {
        return Gen.text().csv().column("col1", random -> "val1");
    }

    @Test
    void csv() {
        final String result = spec()
                .column("col2", random -> "val2,with,commas")
                .column("col3", random -> 123)
                .column("col4", random -> true)
                .rows(3)
                .separator("|")
                .wrapWith("'")
                .wrapIf(obj -> obj.toString().contains(","))
                .get();

        assertThat(result).isEqualTo(String.format("" +
                "col1|col2|col3|col4%n" +
                "val1|'val2,with,commas'|123|true%n" +
                "val1|'val2,with,commas'|123|true%n" +
                "val1|'val2,with,commas'|123|true"));
    }

    @Test
    void csvFromSpecs() {
        final String result = spec()
                .noHeader()
                .rows(1)
                .column("col2", Gen.booleans().probability(1))
                .get();

        assertThat(result).isEqualTo("val1,true");
    }
}
