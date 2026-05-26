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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag({Feature.SIZE, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class SizeWithModelTest {

    private static final int SIZE = 7;

    @Test
    void modelWithSizeCanBeOverriddenInDerivedInstance() {
        final int sizeOverride = SIZE + 5;

        final Model<ListString> model = Instancio.of(ListString.class)
                .size(field(ListString::getList), SIZE)
                .toModel();

        final ListString result = Instancio.of(model)
                .size(field(ListString::getList), sizeOverride)
                .create();

        assertThat(result.getList()).hasSize(sizeOverride);
    }

    @Test
    void modelWithSizeIsPreservedWhenDerivedInstanceDoesNotOverride() {
        final Model<ListString> model = Instancio.of(ListString.class)
                .size(field(ListString::getList), SIZE)
                .toModel();

        final ListString result = Instancio.of(model).create();

        assertThat(result.getList()).hasSize(SIZE);
    }

    @Nested
    class SetModel {

        record Holder(ListString listString) {}

        @Test
        void originalModelSize() {
            final Model<ListString> model = Instancio.of(ListString.class)
                    .size(field(ListString::getList), SIZE)
                    .toModel();

            final Holder result = Instancio.of(Holder.class)
                    .setModel(field(Holder::listString), model)
                    .create();

            assertThat(result.listString.getList()).hasSize(SIZE);
        }

        @Test
        void overrideModelSize() {
            final int sizeOverride = SIZE + 5;

            final Model<ListString> model = Instancio.of(ListString.class)
                    .size(field(ListString::getList), SIZE)
                    .toModel();

            final Holder result = Instancio.of(Holder.class)
                    .setModel(field(Holder::listString), model)
                    .size(field(ListString::getList), sizeOverride)
                    .create();

            assertThat(result.listString.getList()).hasSize(sizeOverride);
        }
    }
}
