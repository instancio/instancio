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
package org.instancio.test.features.generator.misc;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.root;
import static org.instancio.Select.types;

@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR})
@ExtendWith(InstancioExtension.class)
class EmitGeneratorSelectorsTest {

    @WithSettings
    private final Settings settings = Settings.create()
            // restrict generated random values to -1
            // to differentiate them from values passed via emit()
            .set(Keys.INTEGER_MIN, -1)
            .set(Keys.INTEGER_MAX, -1)
            // ensure enough room for all items
            .set(Keys.COLLECTION_MIN_SIZE, 10)
            .set(Keys.COLLECTION_MAX_SIZE, 10);

    @Test
    void eachSelectorShouldHaveItsOwnEmitItems() {
        final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                .generate(all(all(int.class), all(Integer.class)), gen -> gen.emit().items(1, 2, 3))
                .create();

        assertThat(result)
                .hasSize(10)
                .extracting(IntegerHolder::getPrimitive)
                .startsWith(1, 2, 3)
                .filteredOn(it -> it == -1).hasSize(7);

        assertThat(result)
                .hasSize(10)
                .extracting(IntegerHolder::getWrapper)
                .startsWith(1, 2, 3)
                .filteredOn(it -> it == -1).hasSize(7);
    }

    @Test
    void usingAllInts_listOfIntegerHolders() {
        final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                .generate(allInts(), gen -> gen.emit().items(1, 2, 3))
                .create();

        assertThat(result)
                .hasSize(10)
                .extracting(IntegerHolder::getPrimitive)
                .startsWith(1, 3)
                .filteredOn(it -> it == -1).hasSize(8);

        assertThat(result)
                .hasSize(10)
                .extracting(IntegerHolder::getWrapper)
                .startsWith(2)
                .filteredOn(it -> it == -1).hasSize(9);
    }


    @Test
    void usingAllInts_singleFieldValue() {
        final IntHolder result = Instancio.of(IntHolder.class)
                .generate(allInts(), gen -> gen.emit().items(-1))
                .create();

        assertThat(result.value).isEqualTo(-1);
    }

    @Test
    void usingRootSelector() {
        final String result = Instancio.of(String.class)
                .generate(root(), gen -> gen.emit().items("foo"))
                .create();

        assertThat(result).isEqualTo("foo");
    }

    @Test
    void usingPredicateSelector() {
        final IntegerHolder result = Instancio.of(IntegerHolder.class)
                .generate(types(t -> t == int.class || t == Integer.class), gen -> gen.emit().items(1, 2))
                .create();

        assertThat(result.getPrimitive()).isEqualTo(1);
        assertThat(result.getWrapper()).isEqualTo(2);
    }

    private static class IntHolder {
        @SuppressWarnings("unused")
        int value;
    }
}
