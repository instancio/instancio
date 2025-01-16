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
package org.instancio.test.features.fill;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FillType;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.IGNORE, Feature.FILL})
@ExtendWith(InstancioExtension.class)
class FillIgnoreTest {

    @EnumSource(FillType.class)
    @ParameterizedTest
    void shouldIgnoreSpecifiedTarget(final FillType fillType) {
        final StringsAbc object = StringsAbc.builder()
                .a("A")
                .def(StringsDef.builder()
                        .d("D")
                        .ghi(StringsGhi.builder().i("I").build())
                        .build())
                .build();

        Instancio.ofObject(object)
                .withFillType(fillType)
                .ignore(all(StringsGhi.class))
                .set(field(StringsAbc::getB), "B")
                .set(field(StringsDef::getE), "E")
                .fill();

        assertThat(object.getA()).isEqualTo("A");
        assertThat(object.getB()).isEqualTo("B");
        assertThat(object.getDef().getD()).isEqualTo("D");
        assertThat(object.getDef().getE()).isEqualTo("E");
        // value that was set manually
        assertThat(object.getDef().getGhi().getI()).isEqualTo("I");
        // the remaining fields are null since the class is ignored
        assertThat(object.getDef().getGhi().getG()).isNull();
        assertThat(object.getDef().getGhi().getH()).isNull();
    }

    @Test
    void ignoreInitialisedField() {
        final String initialValue = "foo";
        final StringHolder object = new StringHolder(initialValue);

        Instancio.ofObject(object)
                .ignore(field(StringHolder::getValue))
                .fill();

        assertThat(object.getValue()).isEqualTo(initialValue);
    }
}
