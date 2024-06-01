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
package org.instancio.test.features.cartesianproduct;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringAndPrimitiveFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class CartesianProductSettingsTest {

    private static final int INT_ONE = 10;
    private static final int INT_TWO = 20;

    @Test
    void withSetting() {
        List<StringAndPrimitiveFields> results = Instancio.ofCartesianProduct(StringAndPrimitiveFields.class)
                .with(field(StringAndPrimitiveFields::getIntOne), INT_ONE)
                .with(field(StringAndPrimitiveFields::getIntTwo), INT_TWO)
                .withSetting(Keys.STRING_MAX_LENGTH, 1)
                .create();

        assertResults(results);
    }

    @Test
    void withSettings() {
        List<StringAndPrimitiveFields> results = Instancio.ofCartesianProduct(StringAndPrimitiveFields.class)
                .with(field(StringAndPrimitiveFields::getIntOne), INT_ONE)
                .with(field(StringAndPrimitiveFields::getIntTwo), INT_TWO)
                .withSettings(Settings.create().set(Keys.STRING_MAX_LENGTH, 1))
                .create();

        assertResults(results);
    }

    private static void assertResults(final List<StringAndPrimitiveFields> results) {
        assertThat(results)
                .singleElement()
                .satisfies(result -> {
                    assertThat(result.getOne()).hasSize(1);
                    assertThat(result.getTwo()).hasSize(1);
                    assertThat(result.getIntOne()).isEqualTo(INT_ONE);
                    assertThat(result.getIntTwo()).isEqualTo(INT_TWO);
                });
    }
}
