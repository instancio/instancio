/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.ignore;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Mode;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.IGNORE, Feature.SET})
@ExtendWith(InstancioExtension.class)
class IgnoreAndSetTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.MODE, Mode.LENIENT);

    @Test
    void setThenIgnore() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .set(allStrings(), "foo")
                .ignore(allStrings())
                .create();

        assertThat(result.getValue()).isNull();
    }

    @Test
    void ignoreThenSet() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .ignore(allStrings())
                .set(allStrings(), "foo")
                .create();

        assertThat(result.getValue()).isNull();
    }

}
