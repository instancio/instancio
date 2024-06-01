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
package org.instancio.test.features.withunique;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.lists.ListInteger;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

@FeatureTag(Feature.WITH_UNIQUE)
@ExtendWith(InstancioExtension.class)
class WithUniqueBasicTest {

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, 1)
            .set(Keys.INTEGER_MAX, 10);

    @Test
    void create() {
        final int size = 10;
        final ListInteger result = Instancio.of(ListInteger.class)
                .generate(all(List.class), gen -> gen.collection().size(size))
                .withUnique(all(Integer.class))
                .create();

        assertThat(result.getList())
                .hasSize(size)
                .doesNotHaveDuplicates();
    }
}
