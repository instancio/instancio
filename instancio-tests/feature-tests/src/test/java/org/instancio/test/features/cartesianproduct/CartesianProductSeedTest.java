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
package org.instancio.test.features.cartesianproduct;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.CARTESIAN_PRODUCT, Feature.WITH_SEED, Feature.WITH_SEED_ANNOTATION})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ExtendWith(InstancioExtension.class)
class CartesianProductSeedTest {

    private static final long SEED = 123;

    @SuppressWarnings("FieldCanBeLocal")
    private static List<Triplet<Boolean, Boolean, UUID>> first;

    @Seed(SEED)
    @Order(1)
    @Test
    @DisplayName("1. Seed via @Seed annotation - create reference object")
    void first() {
        first = Instancio.ofCartesianProduct(new TypeToken<Triplet<Boolean, Boolean, UUID>>() {})
                .with(field(Triplet.class, "left"), true, false)
                .with(field(Triplet.class, "mid"), true, false)
                .create();

        final Set<UUID> uuids = first.stream().map(Triplet::getRight).collect(Collectors.toSet());
        assertThat(uuids)
                .as("Fields that are not part of cartesian product should be random")
                .hasSize(4);

        assertThat(first)
                .extracting(Triplet::getLeft)
                .containsExactly(true, true, false, false);

        assertThat(first)
                .extracting(Triplet::getMid)
                .containsExactly(true, false, true, false);
    }

    @Seed(SEED)
    @Order(2)
    @Test
    @DisplayName("2. Seed via @Seed annotation")
    void second() {
        final List<Triplet<Boolean, Boolean, UUID>> result = Instancio.ofCartesianProduct(new TypeToken<Triplet<Boolean, Boolean, UUID>>() {})
                .with(field(Triplet.class, "left"), true, false)
                .with(field(Triplet.class, "mid"), true, false)
                .create();

        assertThat(result).isEqualTo(first);
    }

    @Order(3)
    @Test
    @DisplayName("3. Seed via API builder")
    void third() {
        final List<Triplet<Boolean, Boolean, UUID>> result = Instancio.ofCartesianProduct(new TypeToken<Triplet<Boolean, Boolean, UUID>>() {})
                .withSeed(SEED)
                .with(field(Triplet.class, "left"), true, false)
                .with(field(Triplet.class, "mid"), true, false)
                .create();

        assertThat(result).isEqualTo(first);
    }

    @Order(4)
    @Test
    @DisplayName("4. Seed via Settings")
    void fourth() {
        final List<Triplet<Boolean, Boolean, UUID>> result = Instancio.ofCartesianProduct(new TypeToken<Triplet<Boolean, Boolean, UUID>>() {})
                .withSettings(Settings.create().set(Keys.SEED, SEED))
                .with(field(Triplet.class, "left"), true, false)
                .with(field(Triplet.class, "mid"), true, false)
                .create();

        assertThat(result).isEqualTo(first);
    }
}
