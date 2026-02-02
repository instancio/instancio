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
package org.instancio.creation.generics;

import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.container.ItemArrayContainer;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.test.support.util.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class ItemArrayContainerCreationTest extends CreationTestTemplate<ItemArrayContainer<Integer, String>> {

    @Override
    protected void verify(ItemArrayContainer<Integer, String> result) {
        assertThat(result.getItemArrayX())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .isInstanceOf(Item[].class)
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(Integer.class));

        assertThat(result.getItemArrayY())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .isInstanceOf(Item[].class)
                .hasOnlyElementsOfType(Item.class)
                .allSatisfy(it -> assertThat(it.getValue()).isInstanceOf(String.class));
    }
}
