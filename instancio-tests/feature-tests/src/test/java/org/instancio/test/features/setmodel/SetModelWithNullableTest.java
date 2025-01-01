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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.MODEL, Feature.SET_MODEL, Feature.WITH_NULLABLE})
@ExtendWith(InstancioExtension.class)
class SetModelWithNullableTest {

    @Test
    void withNullableModelField() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .withNullable(field(StringsDef::getD))
                .toModel();

        final List<StringsAbc> results = Instancio.ofList(StringsAbc.class)
                .size(Constants.SAMPLE_SIZE_DD)
                .setModel(all(StringsDef.class), model)
                .create();

        assertThat(results)
                .extracting(s -> s.getDef().getD())
                .containsNull();
    }

    @Test
    void withNullableModel() {
        final Model<StringsDef> model = Instancio.of(StringsDef.class)
                .set(allStrings(), "def")
                .toModel();

        final List<StringsAbc> results = Instancio.ofList(StringsAbc.class)
                .size(Constants.SAMPLE_SIZE_DD)
                .setModel(all(StringsDef.class), model)
                .withNullable(all(StringsDef.class))
                .create();

        assertThat(results)
                .extracting(StringsAbc::getDef)
                .containsNull()
                .filteredOn(Objects::nonNull)
                .isNotEmpty()
                .allSatisfy(res -> assertThatObject(res).hasAllFieldsOfTypeEqualTo(String.class, "def"));
    }
}
