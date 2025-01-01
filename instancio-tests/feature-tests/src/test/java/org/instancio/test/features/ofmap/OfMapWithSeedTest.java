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
package org.instancio.test.features.ofmap;

import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag({
        Feature.OF_MAP,
        Feature.WITH_SEED,
        Feature.WITH_SEED_ANNOTATION
})
@ExtendWith(InstancioExtension.class)
class OfMapWithSeedTest {

    @Test
    void withSeed() {
        final Map<UUID, String> map1 = Instancio.ofMap(UUID.class, String.class).withSeed(123).create();
        final Map<UUID, String> map2 = Instancio.ofMap(UUID.class, String.class).withSeed(123).create();
        assertThat(map1).isEqualTo(map2);
    }

    @Test
    void withSettingsSeed() {
        final Settings settings = Settings.create().set(Keys.SEED, 123L);
        final Map<UUID, String> map1 = Instancio.ofMap(UUID.class, String.class).withSettings(settings).create();
        final Map<UUID, String> map2 = Instancio.ofMap(UUID.class, String.class).withSettings(settings).create();
        assertThat(map1).isEqualTo(map2);
    }

    @Test
    @Seed(-1)
    void withSeedAnnotation() {
        final Result<Map<UUID, String>> result = Instancio.ofMap(UUID.class, String.class).asResult();

        assertThat(result.getSeed()).isEqualTo(-1);
        assertThat(result.get()).isNotEmpty()
                .doesNotContainKey(null)
                .doesNotContainValue(null);
    }
}
