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
package org.instancio.test.features.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Fail.fail;
import static org.instancio.Select.all;
import static org.instancio.test.support.asserts.UnusedSelectorsAssert.assertUnusedSelectorMessage;

@FeatureTag(Feature.MODE)
class StrictModeBasicTest {

    @Test
    void unusedSelectorsInIgnore() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .ignore(all(List.class));

        assertUnusedSelectors(api);
    }

    @Test
    void unusedSelectorsInNullable() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .withNullable(all(List.class));

        assertUnusedSelectors(api);
    }

    @Test
    void unusedSelectorsInSupply() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .supply(all(List.class), Collections::emptyList);

        assertUnusedSelectors(api);
    }

    @Test
    void unusedSelectorsInGenerate() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .generate(all(List.class), gen -> gen.collection().size(0));

        assertUnusedSelectors(api);
    }

    @Test
    void unusedSelectorsInOnComplete() {
        final InstancioApi<StringHolder> api = Instancio.of(StringHolder.class)
                .onComplete(all(List.class), (List<?> list) -> fail("should not be invoked"));

        assertUnusedSelectors(api);
    }

    private static void assertUnusedSelectors(final InstancioApi<StringHolder> api) {
        assertThatThrownBy(api::create)
                .isInstanceOf(UnusedSelectorException.class)
                .satisfies(ex -> assertUnusedSelectorMessage(ex.getMessage())
                        .hasUnusedSelectorCount(1)
                        .containsUnusedSelector(List.class));
    }
}
