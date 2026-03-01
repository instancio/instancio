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
package org.instancio.test.features.values.collection;

import org.instancio.Instancio;
import org.instancio.InstancioGenApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.generator.specs.ShuffleSpec;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class ShuffleSpecTest extends AbstractValueSpecTestTemplate<Collection<String>> {

    private static final List<String> CHOICES = CollectionUtils.asUnmodifiableList("A", "B", "C");

    @Override
    protected ShuffleSpec<String> spec() {
        return Instancio.gen().shuffle(CHOICES);
    }

    @Test
    void shuffleCollection() {
        final List<Collection<String>> results = spec().list(Constants.SAMPLE_SIZE_DDD);

        assertShuffled(results);
    }

    @Test
    void shuffleVararg() {
        final String[] array = {"A", "B", "C"};

        final List<Collection<String>> results = Instancio.gen()
                .shuffle(array)
                .list(Constants.SAMPLE_SIZE_DDD);

        assertShuffled(results);

        // the original input array should not be modified
        assertThat(array).containsExactly("A", "B", "C");
    }

    private static void assertShuffled(final List<Collection<String>> shuffled) {
        final Set<String> unique = shuffled
                .stream()
                .map(list -> String.join("", list))
                .collect(Collectors.toSet());

        assertThat(unique).containsExactlyInAnyOrder("ABC", "ACB", "BAC", "BCA", "CAB", "CBA");
    }

    @Test
    @SuppressWarnings("NullAway")
    void shuffleNullVararg() {
        final InstancioGenApi api = Instancio.gen();

        assertThatThrownBy(() -> api.shuffle((Object[]) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("array must not be null");
    }

    @Test
    @SuppressWarnings("NullAway")
    void shuffleNullCollection() {
        final InstancioGenApi api = Instancio.gen();

        assertThatThrownBy(() -> api.shuffle((Collection<?>) null))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("collection must not be null");
    }
}
