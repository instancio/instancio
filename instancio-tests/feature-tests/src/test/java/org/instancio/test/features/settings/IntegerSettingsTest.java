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
package org.instancio.test.features.settings;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.collections.lists.ListInteger;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.SETTINGS)
@ExtendWith(InstancioExtension.class)
class IntegerSettingsTest {

    private static final int MIN_VALUE = 10000;
    private static final int MAX_VALUE = 10002;

    private static final Settings settings = Settings.create()
            .set(Keys.INTEGER_MIN, MIN_VALUE)
            .set(Keys.INTEGER_MAX, MAX_VALUE)
            // increase collection size for bigger sample
            .set(Keys.COLLECTION_MIN_SIZE, MIN_VALUE)
            .set(Keys.COLLECTION_MAX_SIZE, MAX_VALUE)
            .lock();


    @Test
    @DisplayName("Override MIN and MAX")
    void minMax() {
        final Settings overrides = settings.merge(Settings.create().set(Keys.STRING_NULLABLE, true));
        for (int i = 0; i < 1000; i++) {
            final IntegerHolder result = Instancio.of(IntegerHolder.class).withSettings(overrides).create();
            assertThat(result.getPrimitive()).isBetween(MIN_VALUE, MAX_VALUE);
            assertThat(result.getWrapper()).isBetween(MIN_VALUE, MAX_VALUE);
        }
    }

    @Test
    @DisplayName("Override nullable to true - generates null in Integer fields")
    void nullable() {
        final Settings overrides = settings.merge(Settings.create().set(Keys.INTEGER_NULLABLE, true));
        final Set<Integer> results = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            final IntegerHolder result = Instancio.of(IntegerHolder.class).withSettings(overrides).create();
            results.add(result.getWrapper());
        }
        assertThat(results).containsNull();
    }

    @Test
    @DisplayName("Override nullable to true - does not generate null in collection elements")
    void integerIsNotNullInCollections() {
        final Settings overrides = settings.merge(Settings.create().set(Keys.STRING_NULLABLE, true));
        final ListInteger result = Instancio.of(ListInteger.class).withSettings(overrides).create();
        assertThat(result.getList()).doesNotContainNull();
    }

    @Test
    void integerMinIsSameAsMax() {
        final Integer result = Instancio.of(Integer.class)
                .withSettings(Settings.create()
                        .set(Keys.INTEGER_MIN, 5)
                        .set(Keys.INTEGER_MAX, 5))
                .create();

        assertThat(result).isEqualTo(5);
    }
}
