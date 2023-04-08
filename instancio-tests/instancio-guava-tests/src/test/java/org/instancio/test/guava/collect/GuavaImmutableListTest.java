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
package org.instancio.test.guava.collect;

import com.google.common.collect.ImmutableList;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.types;

@ExtendWith(InstancioExtension.class)
class GuavaImmutableListTest {

    private static final int EXPECTED_SIZE = 10;

    private static class Holder {
        private List<String> List;
    }

    @Test
    void createViaTypeToken() {
        final List<String> result = Instancio.create(new TypeToken<ImmutableList<String>>() {});

        assertThat(result)
                .isInstanceOf(ImmutableList.class)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);
    }

    @Test
    void generatorSpecSize() {
        final List<String> result = Instancio.of(new TypeToken<ImmutableList<String>>() {})
                .generate(types().of(List.class), gen -> gen.collection().size(EXPECTED_SIZE))
                .create();

        assertImmutableList(result);
    }

    @Test
    void generatorSpecSubtype() {
        final Holder result = Instancio.of(Holder.class)
                .generate(all(List.class), gen -> gen.collection().size(EXPECTED_SIZE).subtype(ImmutableList.class))
                .create();

        assertImmutableList(result.List);
    }

    @Test
    void subtype() {
        final Holder result = Instancio.of(Holder.class)
                .subtype(all(List.class), ImmutableList.class)
                .generate(all(List.class), gen -> gen.collection().size(EXPECTED_SIZE))
                .create();

        assertImmutableList(result.List);
    }

    private static void assertImmutableList(final List<String> result) {
        assertThat(result)
                .isInstanceOf(ImmutableList.class)
                .hasSize(EXPECTED_SIZE)
                .doesNotContainNull();
    }
}
