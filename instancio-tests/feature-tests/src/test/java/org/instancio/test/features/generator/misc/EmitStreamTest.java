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
package org.instancio.test.features.generator.misc;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

/**
 * {@code stream()} creates a new root object each time.
 * Therefore, each root object gets its own copy of values to emit.
 */
@FeatureTag({Feature.EMIT_GENERATOR, Feature.STREAM})
@ExtendWith(InstancioExtension.class)
class EmitStreamTest {

    @Test
    void throwsAnExceptionOnUnusedItems() {
        final String[] items = {"foo", "bar", "baz"};

        final Stream<String> stream = Instancio.of(String.class)
                .generate(all(String.class), gen -> gen.emit().items(items))
                .stream()
                .limit(items.length);

        assertThatThrownBy(() -> stream.collect(toList())) // NOSONAR
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "not all the items provided via the 'emit()' method have been consumed",
                        "Remaining items: [bar, baz]");
    }

    @Test
    void ignoreUnusedItems() {
        final String[] items = {"foo", "bar", "baz"};

        final Stream<String> results = Instancio.of(String.class)
                .generate(all(String.class), gen -> gen.emit().items(items).ignoreUnused())
                .stream()
                .limit(items.length);

        assertThat(results)
                .as("Should pick the first value from items for each root object and ignore the rest")
                .containsExactly("foo", "foo", "foo");
    }

    @Test
    void withMultipleItems() {
        final String[] items = {"foo", "bar", "baz"};

        final int outerListSize = 10;

        final List<List<String>> results = Instancio.of(new TypeToken<List<String>>() {})
                .generate(all(List.class), gen -> gen.collection().size(items.length)) // inner list
                .generate(all(String.class), gen -> gen.emit().items(items))
                .stream()
                .limit(outerListSize)
                .collect(toList());

        assertThat(results)
                .hasSize(outerListSize)
                .allMatch(r -> r.equals(Arrays.asList(items)));
    }
}
