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
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.root;

@FeatureTag({Feature.BLANK, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class BlankModelTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void createFromBlankModel() {
        final Model<StringHolder> model = Instancio.ofBlank(StringHolder.class).toModel();

        final StringHolder result = Instancio.create(model);

        assertThat(result.getValue()).isNull();
    }

    @Test
    void createFromBlankModelWithSelector() {
        final Model<StringHolder> model = Instancio.ofBlank(StringHolder.class)
                .set(field(StringHolder::getValue), "foo")
                .toModel();

        final StringHolder result = Instancio.create(model);

        assertThat(result.getValue()).isEqualTo("foo");
    }

    @Test
    void createFromModel_setBlankRootObject() {
        final Model<StringHolder> model = Instancio.of(StringHolder.class).toModel();

        final StringHolder result = Instancio.of(model)
                .setBlank(root())
                .create();

        assertThat(result.getValue()).isNull();
    }

    @Test
    void createFromModelWithSelector_setRootBlankObject() {
        final Model<StringHolder> model = Instancio.of(StringHolder.class)
                .set(fields().named("value"), "foo")
                .toModel();

        final StringHolder result = Instancio.of(model)
                .setBlank(root())
                .create();

        assertThat(result.getValue()).isEqualTo("foo");
    }
}
