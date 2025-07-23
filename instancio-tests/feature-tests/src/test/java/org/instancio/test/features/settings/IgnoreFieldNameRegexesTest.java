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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.internal.util.Sonar;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.conditions.Conditions;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.SETTINGS)
@ExtendWith(InstancioExtension.class)
class IgnoreFieldNameRegexesTest {

    @SuppressWarnings(Sonar.FIELD_NAMING_CONVENTION)
    private static class Pojo {
        private String $$_ignoreField1;
        private String _ignoredField2;
        private String $_nonIgnoredField;
    }

    @Test
    void shouldIgnoreFieldsWithSpecifiedRegexes() {
        final Pojo result = Instancio.of(Pojo.class)
                .withSetting(Keys.IGNORE_FIELD_NAME_REGEXES, "\\$\\$_.*, _.*")
                .create();

        assertThat(result.$$_ignoreField1).isNull();
        assertThat(result._ignoredField2).isNull();
        assertThat(result.$_nonIgnoredField).is(Conditions.RANDOM_STRING);
    }
}
