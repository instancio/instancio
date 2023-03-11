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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR})
@ExtendWith(InstancioExtension.class)
class EmitGeneratorWhenEmptyTest {

    private static final int SIZE = 10;

    @WithSettings
    private final Settings settings = Settings.create()
            // ensure enough room for all items
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @Test
    void whenEmptyEmitNull() {
        final List<Integer> result = Instancio.ofList(Integer.class)
                .generate(all(Integer.class), gen -> gen.emit()
                        .items(-1, -2, -3)
                        .whenEmptyEmitNull())
                .create();

        assertThat(result)
                .hasSize(SIZE)
                .startsWith(-1, -2, -3);

        assertThat(result.subList(3, SIZE)).containsOnlyNulls();
    }

    @Test
    void whenEmptyEmitRandom() {
        final List<Integer> result = Instancio.ofList(Integer.class)
                .generate(all(Integer.class), gen -> gen.emit()
                        .items(-1, -2, -3)
                        .whenEmptyEmitRandom())
                .create();

        assertThat(result)
                .hasSize(SIZE)
                .startsWith(-1, -2, -3);

        assertThat(result.subList(3, SIZE)).allSatisfy(i -> assertThat(i).isPositive());
    }

    @Test
    void whenEmptyEmitError() {
        final InstancioApi<List<Integer>> api = Instancio.ofList(Integer.class)
                .generate(all(Integer.class), gen -> gen.emit()
                        .items(-1, -2, -3)
                        .whenEmptyThrowException());

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("No items left to emit() for class Integer");
    }

    @Test
    void whenEmptyEmitErrorWithField() {
        final InstancioApi<List<IntegerHolder>> api = Instancio.ofList(IntegerHolder.class)
                .generate(all(Integer.class), gen -> gen.emit()
                        .items(-1, -2, -3)
                        .whenEmptyThrowException());

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessage("No items left to emit() for field IntegerHolder.wrapper");
    }
}
