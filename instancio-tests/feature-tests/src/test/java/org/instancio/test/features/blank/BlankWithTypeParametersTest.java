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
package org.instancio.test.features.blank;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@FeatureTag({Feature.BLANK, Feature.WITH_TYPE_PARAMETERS})
@ExtendWith(InstancioExtension.class)
class BlankWithTypeParametersTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    @SuppressWarnings("unchecked")
    void emptyList() {
        final List<StringHolder> results = Instancio.ofBlank(List.class)
                .withTypeParameters(StringHolder.class)
                .create();

        assertThat(results).isEmpty();
    }

    @Test
    @SuppressWarnings("unchecked")
    void nonEmptyList() {
        final List<StringHolder> results = Instancio.ofBlank(List.class)
                .withTypeParameters(StringHolder.class)
                .generate(types().of(Collection.class), gen -> gen.collection().size(5))
                .create();

        assertThat(results).hasSize(5)
                .allSatisfy(result -> assertThat(result.getValue()).isNull());
    }
}
