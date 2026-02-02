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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignMultipleDestinationsTest {

    @Test
    void multipleDestinations() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .set(field(StringsAbc::getB), "B")
                .assign(Assign.given(field(StringsAbc::getB))
                        .is("B")
                        .set(field(StringsAbc::getA), "A")
                        .set(field(StringsAbc::getC), "C")
                        .set(field(StringsDef::getE), "E")
                        .set(field(StringsGhi::getH), "H")
                        .set(field(StringsGhi::getI), "I"))
                .create();

        assertThat(result.a).isEqualTo("A");
        assertThat(result.b).isEqualTo("B");
        assertThat(result.c).isEqualTo("C");
        assertThat(result.def.e).isEqualTo("E");
        assertThat(result.def.ghi.h).isEqualTo("H");
        assertThat(result.def.ghi.i).isEqualTo("I");

        // random values
        assertThat(result.def.d).hasSizeGreaterThan(1);
        assertThat(result.def.f).hasSizeGreaterThan(1);
        assertThat(result.def.ghi.g).hasSizeGreaterThan(1);
    }
}
