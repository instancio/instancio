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
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class NestedPairTripletMapPairItemCreationTest extends CreationTestTemplate
        <Pair<Triplet<Boolean, Character, LocalDate>, Map<Integer, Pair<Integer, Item<Integer>>>>> {

    @Override
    protected void verify(final Pair<Triplet<Boolean, Character, LocalDate>, Map<Integer, Pair<Integer, Item<Integer>>>> result) {
        assertThat(result.getLeft().getLeft()).isInstanceOf(Boolean.class);
        assertThat(result.getLeft().getMid()).isInstanceOf(Character.class);
        assertThat(result.getLeft().getRight()).isInstanceOf(LocalDate.class);

        assertThat(result.getRight()).isInstanceOf(Map.class);
        assertThat(result.getRight().entrySet()).isNotEmpty()
                .allSatisfy(entry -> {
                    assertThat(entry.getKey()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue().getLeft()).isInstanceOf(Integer.class);
                    assertThat(entry.getValue().getRight().getValue()).isInstanceOf(Integer.class);
                });
    }
}
