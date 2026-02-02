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
package org.instancio.test.features.fill;

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

@FeatureTag({Feature.BLANK, Feature.FILL})
@ExtendWith(InstancioExtension.class)
class FillSetBlankTest {

    @Test
    void setBlankShouldOverwriteInitialisedFields() {
        final StringsAbc object = StringsAbc.builder()
                .a("A")
                .b("B")
                .def(StringsDef.builder()
                        .d("D")
                        .ghi(StringsGhi.builder().i("I").build())
                        .build())
                .build();

        Instancio.ofObject(object)
                .setBlank(field(StringsAbc::getA))
                .setBlank(field(StringsDef::getGhi))
                .fill();

        assertThat(object.getA()).isNull();
        assertThat(object.getB()).isEqualTo("B");
        assertThat(object.getDef().getD()).isEqualTo("D");
        assertThat(object.getDef().getGhi()).hasAllNullFieldsOrProperties();
    }
}
