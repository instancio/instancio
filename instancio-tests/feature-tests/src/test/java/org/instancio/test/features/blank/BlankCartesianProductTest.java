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
package org.instancio.test.features.blank;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.BLANK, Feature.CARTESIAN_PRODUCT})
@ExtendWith(InstancioExtension.class)
class BlankCartesianProductTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void setBlankRoot() {
        final List<StringsAbc> results = Instancio.ofCartesianProduct(StringsAbc.class)
                .setBlank(root())
                .with(field(StringsAbc::getA), "A")
                .with(field(StringsAbc::getB), "B1", "B2")
                .create();

        assertThat(results).allSatisfy(result -> {
            assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
            assertThatObject(result.def.ghi).hasAllFieldsOfTypeSetToNull(String.class);
        });

        assertThat(results).extracting(StringsAbc::getA).containsExactly("A", "A");
        assertThat(results).extracting(StringsAbc::getB).containsExactly("B1", "B2");
    }

    @Test
    void setBlankNestedPojo() {
        final List<StringsAbc> results = Instancio.ofCartesianProduct(StringsAbc.class)
                .setBlank(field(StringsAbc::getDef))
                .with(field(StringsAbc::getA), "A")
                .with(field(StringsAbc::getB), "B1", "B2")
                .create();

        assertThat(results).allSatisfy(result -> {
            assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
            assertThatObject(result.def.ghi).hasAllFieldsOfTypeSetToNull(String.class);
        });

        assertThat(results).extracting(StringsAbc::getA).containsExactly("A", "A");
        assertThat(results).extracting(StringsAbc::getB).containsExactly("B1", "B2");
    }

}
