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
package org.instancio.test.features.size;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.LinkedList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

@FeatureTag(Feature.SIZE)
@ExtendWith(InstancioExtension.class)
class SizePrecedenceTest {

    private static final int SIZE = 7;

    @Test
    void sizeHasHigherPrecedenceThanGenerate_regardlessOfDeclarationOrder() {
        final ListString result = Instancio.of(ListString.class)
                .size(field(ListString::getList), SIZE)
                .generate(field(ListString::getList), gen -> gen.collection().subtype(LinkedList.class).size(SIZE + 1))
                .create();

        assertThat(result.getList())
                .hasSize(SIZE)
                .isExactlyInstanceOf(LinkedList.class); // generate() subtype is preserved
    }


    @Test
    void sizeHasHigherPrecedenceThanGenerate_regardlessOfDeclarationOrder_usingSetModel() {
        record Holder(ListString listString) {}

        final Model<ListString> model = Instancio.of(ListString.class)
                .size(field(ListString::getList), SIZE)
                .toModel();

        final Holder result = Instancio.of(Holder.class)
                .setModel(all(ListString.class), model)
                .generate(field(ListString::getList), gen -> gen.collection().subtype(LinkedList.class).size(SIZE + 1))
                .create();

        assertThat(result.listString.getList())
                .hasSize(SIZE)
                .isExactlyInstanceOf(LinkedList.class);
    }

    @Test
    void givenSameSizeSelectors_lastSizeCallWins() {
        final ListString result = Instancio.of(ListString.class)
                .size(field(ListString::getList), SIZE + 1)
                .size(field(ListString::getList), SIZE)
                .create();

        assertThat(result.getList()).hasSize(SIZE);
    }

}
