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
package org.instancio.test.features.generator.map;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Gender;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.EnumMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;

@ExtendWith(InstancioExtension.class)
class EnumMapTest {

    private static final TypeToken<EnumMap<Gender, String>> ENUM_MAP = new TypeToken<EnumMap<Gender, String>>() {};

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.MAP_MIN_SIZE, 1)
            .set(Keys.MAP_MAX_SIZE, Gender.values().length);

    @Test
    void create() {
        assertThat(Instancio.create(ENUM_MAP))
                .isNotEmpty()
                .isExactlyInstanceOf(EnumMap.class);
    }

    @Test
    void createEmpty() {
        final EnumMap<Gender, String> result = Instancio.of(ENUM_MAP)
                .generate(root(), gen -> gen.map().size(0))
                .create();

        assertThat(result).isEmpty();
    }

    @RepeatedTest(10)
    void with() {
        final EnumMap<Gender, String> result = Instancio.of(ENUM_MAP)
                .generate(root(), gen -> gen.map().with(Gender.OTHER, "foo"))
                .create();

        // Due to possible collisions of random keys,
        // the only guaranteed entry is the one we added
        assertThat(result)
                .hasSizeGreaterThanOrEqualTo(1)
                .containsEntry(Gender.OTHER, "foo");
    }

    @Test
    void ofMap() {
        final Map<Gender, String> result = Instancio.ofMap(Gender.class, String.class)
                .subtype(root(), EnumMap.class)
                .create();

        assertThat(result)
                .isNotEmpty()
                .isExactlyInstanceOf(EnumMap.class);
    }
}
