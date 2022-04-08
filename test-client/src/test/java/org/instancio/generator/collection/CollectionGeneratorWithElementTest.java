/*
 *  Copyright 2022 the original author or authors.
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
package org.instancio.generator.collection;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.features.Feature;
import org.instancio.features.FeatureTest;
import org.instancio.pojo.collections.lists.ListLong;
import org.instancio.pojo.collections.sets.SetLong;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Bindings.all;

@FeatureTest(Feature.COLLECTION_GENERATOR_WITH)
class CollectionGeneratorWithElementTest {
    private static final Long[] EXPECTED_LONGS = {1L, 2L, 3L};

    @Test
    void setWithElements() {
        final SetLong result = Instancio.of(SetLong.class)
                .generate(all(Set.class), gen -> gen.collection().with(EXPECTED_LONGS))
                .create();

        assertThat(result.getSet()).contains(EXPECTED_LONGS);
    }

    @Test
    void createSetDirectly() {
        final Set<Long> result = Instancio.of(new TypeToken<Set<Long>>() {})
                .generate(all(Set.class), gen -> gen.collection().with(EXPECTED_LONGS))
                .create();

        assertThat(result).contains(EXPECTED_LONGS);
    }

    @Test
    void listWithElements() {
        final ListLong result = Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> gen.collection().with(EXPECTED_LONGS))
                .create();

        assertThat(result.getList()).contains(EXPECTED_LONGS);
    }

    @Test
    void listWithElementsAndMaxSizeZero() {
        final ListLong result = Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> gen.collection().maxSize(0).with(EXPECTED_LONGS))
                .create();

        assertThat(result.getList()).containsOnly(EXPECTED_LONGS);
    }

    @Test
    void withNullOrEmpty() {
        assertValidation(null);
        assertValidation();
    }

    private void assertValidation(final Object... arg) {
        assertThatThrownBy(() -> Instancio.of(ListLong.class)
                .generate(all(List.class), gen -> gen.collection().with(arg))
                .create())
                .isInstanceOf(InstancioApiException.class)
                .hasMessage("'collection().with(...)' must contain at least one element");
    }
}
