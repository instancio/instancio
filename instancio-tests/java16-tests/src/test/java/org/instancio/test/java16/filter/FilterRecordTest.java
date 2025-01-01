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
package org.instancio.test.java16.filter;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.StringsAbcRecord;
import org.instancio.test.support.java16.record.StringsDefRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag(Feature.FILTER)
@NonDeterministicTag("Small probability of max retries exceeded error")
@ExtendWith(InstancioExtension.class)
class FilterRecordTest {

    @Test
    void filterRecordFields() {
        final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                .generate(allStrings(), gen -> gen.oneOf("A", "B", "C"))
                .filter(field(StringsAbcRecord::a), "A"::equals)
                .filter(field(StringsAbcRecord::b), "B"::equals)
                .filter(field(StringsAbcRecord::c), "C"::equals)
                .create();

        assertThat(result.a()).isEqualTo("A");
        assertThat(result.b()).isEqualTo("B");
        assertThat(result.c()).isEqualTo("C");
    }

    @Test
    void filterRecord() {
        final StringsAbcRecord result = Instancio.of(StringsAbcRecord.class)
                .generate(allStrings(), gen -> gen.oneOf("D", "E"))
                .filter(field(StringsAbcRecord::def), (StringsDefRecord def) ->
                        def.d().equals("D") && def.e().equals("E"))
                .create();

        assertThat(result.def().d()).isEqualTo("D");
        assertThat(result.def().e()).isEqualTo("E");
    }
}
