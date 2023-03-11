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
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

    public static Stream<Arguments> getSelectors() {
        return Stream.of(
                Arguments.of(allInts()),
                Arguments.of(all(all(int.class), all(Integer.class)))
        );
    }

    @MethodSource("getSelectors")
    @ParameterizedTest
    void eachSelectorShouldHaveItsOwnEmitItems(final TargetSelector selector) {
        final List<IntegerHolder> result = Instancio.ofList(IntegerHolder.class)
                .generate(selector, gen -> gen.emit().items(1, 2, 3))
                .create();

        assertThat(result).extracting(IntegerHolder::getPrimitive)
                .startsWith(1, 2, 3)
                .filteredOn(it -> it == -1).hasSize(7);

        assertThat(result).extracting(IntegerHolder::getWrapper)
                .startsWith(1, 2, 3)
                .filteredOn(it -> it == -1).hasSize(7);
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

    @Test
    void usingPrimitiveAndWrapperSelector() {
        final InstancioApi<IntHolder> api = Instancio.of(IntHolder.class)
                .generate(allInts(), gen -> gen.emit().items(1));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("not all the items provided via emit() have been consumed");
    }

    private static class IntHolder {
        @SuppressWarnings("unused")
        int value;
    }
}
