/*
 *  Copyright 2022-2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.features.generator.collection;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.collections.lists.ListInteger;
import org.instancio.test.support.pojo.collections.sets.SetInteger;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.root;
import static org.instancio.Select.types;

@NonDeterministicTag
@FeatureTag({Feature.GENERATE, Feature.COLLECTION_GENERATOR_UNIQUE})
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorUniqueTest {

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.INTEGER_MIN, 1)
            .set(Keys.INTEGER_MAX, 5);

    @Nested
    class ListTest {

        @WithSettings
        private final Settings settings = SETTINGS;

        @Test
        void unique() {
            final ListInteger result = Instancio.of(ListInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().size(5))
                    .create();

            assertThat(result.getList()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
        }

        @Test
        void uniqueNullableElements() {
            final ListInteger result = Instancio.of(ListInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().nullableElements().size(6))
                    .create();

            assertThat(result.getList()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, null);
        }

        @Test
        void impossibleUnique() {
            final ListInteger result = Instancio.of(ListInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().size(100))
                    .create();

            assertThat(result.getList()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
        }

        @Test
        void impossibleUniqueNullableElements() {
            final ListInteger result = Instancio.of(ListInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().nullableElements().size(100))
                    .create();

            assertThat(result.getList()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, null);
        }

        @Test
        void ofList() {
            final List<Integer> result = Instancio.ofList(Integer.class)
                    .generate(root(), gen -> gen.collection().unique().nullableElements().size(100))
                    .create();

            assertThat(result).containsExactlyInAnyOrder(1, 2, 3, 4, 5, null);
        }
    }

    @Nested
    class SetTest {

        @WithSettings
        private final Settings settings = SETTINGS;

        @Test
        void unique() {
            final SetInteger result = Instancio.of(SetInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().size(5))
                    .create();

            assertThat(result.getSet()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
        }

        @Test
        void uniqueNullableElements() {
            final SetInteger result = Instancio.of(SetInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().nullableElements().size(6))
                    .create();

            assertThat(result.getSet()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, null);
        }

        @Test
        void impossibleUnique() {
            final SetInteger result = Instancio.of(SetInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().size(100))
                    .create();

            assertThat(result.getSet()).containsExactlyInAnyOrder(1, 2, 3, 4, 5);
        }

        @Test
        void impossibleUniqueNullableElements() {
            final SetInteger result = Instancio.of(SetInteger.class)
                    .generate(types().of(Collection.class), gen -> gen.collection().unique().nullableElements().size(100))
                    .create();

            assertThat(result.getSet()).containsExactlyInAnyOrder(1, 2, 3, 4, 5, null);
        }

        @Test
        void ofSet() {
            final Set<Integer> result = Instancio.ofSet(Integer.class)
                    .generate(root(), gen -> gen.collection().unique().nullableElements().size(100))
                    .create();

            assertThat(result).containsExactlyInAnyOrder(1, 2, 3, 4, 5, null);
        }
    }

}