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
import org.instancio.InstancioObjectApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.FillType;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag({Feature.FILL, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class FillWithNullableTest {

    @EnumSource(value = FillType.class, names = {"POPULATE_NULLS", "POPULATE_NULLS_AND_DEFAULT_PRIMITIVES"})
    @ParameterizedTest
    void withNullableField(final FillType fillType) {
        final AtomicBoolean hasNullE = new AtomicBoolean();
        final AtomicBoolean hasNullI = new AtomicBoolean();
        final AtomicBoolean hasNullGhi = new AtomicBoolean();

        for (int i = 0; i < Constants.SAMPLE_SIZE_DD; i++) {
            final StringsAbc object = StringsAbc.builder()
                    .a("A")
                    .def(StringsDef.builder()
                            .d("D")
                            .build())
                    .build();

            Instancio.ofObject(object)
                    .withFillType(fillType)
                    .withNullable(all(
                            field(StringsDef::getE),
                            field(StringsGhi::getI),
                            all(StringsGhi.class)))
                    .fill();

            if (object.getDef().getE() == null) {
                hasNullE.set(true);
            }
            if (object.getDef().getGhi() != null && object.getDef().getGhi().getI() == null) {
                hasNullI.set(true);
            }
            if (object.getDef().getGhi() == null) {
                hasNullGhi.set(true);
            }

            assertThat(object.getA()).isEqualTo("A");
            assertThat(object.getDef().getD()).isEqualTo("D");
        }

        assertThat(hasNullE).isTrue();
        assertThat(hasNullI).isTrue();
        assertThat(hasNullGhi).isTrue();
    }

    @FeatureTag(Feature.UNSUPPORTED)
    @Test
    void withNullable_notSupported_forInitialisedFields() {
        final InstancioObjectApi<StringHolder> api = Instancio.ofObject(new StringHolder("foo"))
                .withNullable(field(StringHolder::getValue));

        assertThatThrownBy(api::fill)
                .isExactlyInstanceOf(UnusedSelectorException.class)
                .hasMessageContaining("Unused selector in: withNullable()");
    }
}
