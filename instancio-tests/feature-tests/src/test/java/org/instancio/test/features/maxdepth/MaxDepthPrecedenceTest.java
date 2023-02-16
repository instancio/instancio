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
package org.instancio.test.features.maxdepth;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({Feature.MAX_DEPTH, Feature.SETTINGS})
@ExtendWith(InstancioExtension.class)
class MaxDepthPrecedenceTest {

    @Test
    void maxDepthViaBuilderApiTakesPrecedenceOverSettings() {
        final Address result = Instancio.of(Address.class)
                .withMaxDepth(0)
                .withSettings(Settings.create().set(Keys.MAX_DEPTH, Integer.MAX_VALUE))
                .create();

        assertThat(result).hasAllNullFieldsOrProperties();
    }
}
