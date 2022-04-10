/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.client.generator.array;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.arrays.ArrayCharSequence;
import org.instancio.test.support.pojo.arrays.TwoArraysOfItemInterfaceString;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.util.Sonar;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.all;

@FeatureTag(Feature.ARRAY_GENERATOR_TYPE)
@ExtendWith(InstancioExtension.class)
class ArrayTypeTest {

    @Test
    @Disabled
    @FeatureTag(Feature.UNSUPPORTED)
    @SuppressWarnings(Sonar.DISABLED_TEST)
    void shouldCreateArrayOfSpecifiedType2() {
        final TwoArraysOfItemInterfaceString result = Instancio.of(TwoArraysOfItemInterfaceString.class)
                .generate(all(ItemInterface[].class), gen -> gen.array().type(Item[].class))
                .create();

        assertThat(result.getArray1()).isNotEmpty().allSatisfy(it -> {
            assertThat(it).isNotNull();
            assertThat(it.getValue()).isNotBlank();
        });
        assertThat(result.getArray2()).isNotEmpty().allSatisfy(it -> {
            assertThat(it).isNotNull();
            assertThat(it.getValue()).isNotBlank();
        });
    }

    @Test
    @DisplayName("Create array with non-generic component type")
    void nonGenericSubtype() {
        final ArrayCharSequence result = Instancio.of(ArrayCharSequence.class)
                .generate(all(CharSequence[].class), gen -> gen.array().type(String[].class))
                .create();

        assertThat(result.getArray())
                .isNotEmpty()
                .allSatisfy(it -> assertThat(it).isNotBlank());
    }
}
