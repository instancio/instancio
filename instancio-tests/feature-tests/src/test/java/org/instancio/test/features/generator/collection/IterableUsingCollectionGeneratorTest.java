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
package org.instancio.test.features.generator.collection;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.root;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class IterableUsingCollectionGeneratorTest {

    @Test
    void create() {
        final Iterable<String> result = Instancio.create(new TypeToken<Iterable<String>>() {});

        assertThat(result).isNotEmpty()
                .isExactlyInstanceOf(ArrayList.class)
                .hasOnlyElementsOfType(String.class);
    }

    @Test
    void collectionGeneratorSpecSupportsIterable() {
        final Iterable<String> result = Instancio.of(new TypeToken<Iterable<String>>() {})
                .generate(root(), gen -> gen.collection().size(10))
                .create();

        assertThat(result).hasSize(10).hasOnlyElementsOfTypes(String.class);
    }

    @Test
    void assignCollectionToIterable() {
        class IterableHolder {
            @Nullable Iterable<String> iterable;
        }

        final IterableHolder result = Instancio.of(IterableHolder.class)
                .generate(field("iterable"), gen -> gen.collection().size(10))
                .create();

        assertThat(result.iterable)
                .hasSize(10)
                .allSatisfy(s -> assertThat(s).isNotBlank());
    }

}
