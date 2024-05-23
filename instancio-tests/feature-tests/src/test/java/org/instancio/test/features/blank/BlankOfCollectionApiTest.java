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
package org.instancio.test.features.blank;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({
        Feature.BLANK,
        Feature.OF_LIST,
        Feature.OF_MAP,
        Feature.OF_SET,
})
@ExtendWith(InstancioExtension.class)
class BlankOfCollectionApiTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void ofList() {
        final List<StringHolder> results = Instancio.ofList(StringHolder.class)
                .withBlank(all(StringHolder.class))
                .create();

        assertThat(results).isNotEmpty()
                .allSatisfy(obj -> assertThatObject(obj).hasAllFieldsOfTypeSetToNull(String.class));
    }

    @Test
    void ofListWithSelector() {
        final List<StringHolder> results = Instancio.ofList(StringHolder.class)
                .withBlank(all(StringHolder.class))
                .set(field(StringHolder::getValue), "foo")
                .create();

        assertThat(results).isNotEmpty()
                .extracting(StringHolder::getValue)
                .containsOnly("foo");
    }

    @Test
    void ofMap() {
        final Map<StringHolder, Long> results = Instancio.ofMap(StringHolder.class, Long.class)
                // disable FAIL_ON_ERROR because we can't populate a set with more than
                // one element (by default, min size is 2, which would result an an error)
                .withSetting(Keys.FAIL_ON_ERROR, false)
                .withBlank(all(StringHolder.class))
                .create();

        assertThat(results.keySet()).hasSize(1)
                .allSatisfy(obj -> assertThatObject(obj).hasAllFieldsOfTypeSetToNull(String.class));
    }

    @Test
    void ofSet_withFailOnErrorDisabled() {
        final Set<StringHolder> results = Instancio.ofSet(StringHolder.class)
                // disable FAIL_ON_ERROR because we can't populate a set with more than
                // one element (by default, min size is 2, which would result an an error)
                .withSetting(Keys.FAIL_ON_ERROR, false)
                .withBlank(all(StringHolder.class))
                .create();

        assertThat(results).hasSize(1)
                .allSatisfy(obj -> assertThatObject(obj).hasAllFieldsOfTypeSetToNull(String.class));
    }

    @Test
    void ofSet_withFailOnErrorEnabled() {
        final InstancioApi<Set<StringHolder>> api = Instancio.ofSet(StringHolder.class)
                .withBlank(all(StringHolder.class));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioException.class)
                .hasMessageContaining("unable to populate Collection of size");
    }
}
