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
package org.instancio.internal.util;

import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.Generator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.text.TextPatternGenerator;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.inheritance.NonGenericSubclassOfList;
import org.instancio.test.support.pojo.generics.inheritance.NonGenericSubclassOfMap;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TypeUtilsTest {

    @Test
    void getTypeParameterCount() {
        assertThat(TypeUtils.getTypeParameterCount(String.class)).isZero();
        assertThat(TypeUtils.getTypeParameterCount(List.class)).isOne();
        assertThat(TypeUtils.getTypeParameterCount(List[].class)).isOne();
    }

    @Test
    void getArrayClass() {
        assertThat(TypeUtils.getArrayClass(new TypeToken<Integer[]>() {}.get())).isEqualTo(Integer[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<int[]>() {}.get())).isEqualTo(int[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<Item<Integer>[]>() {}.get())).isEqualTo(Item[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<Item<String>[]>() {}.get())).isEqualTo(Item[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<String>() {}.get())).isEqualTo(String[].class);
    }

    @Test
    void getGenericSuperclassTypeArgument() {
        assertThat(TypeUtils.getGenericSuperclassTypeArgument(StringGenerator.class)).isEqualTo(String.class);
        assertThat(TypeUtils.getGenericSuperclassTypeArgument(TextPatternGenerator.class)).isEqualTo(String.class);
        assertThat(TypeUtils.getGenericSuperclassTypeArgument(Bar.class)).isEqualTo(String.class);
    }

    @Test
    void getGenericSuperclassTypeArguments() {
        assertThat(TypeUtils.getGenericSuperclassTypeArguments(String.class))
                .isEmpty();

        assertThat(TypeUtils.getGenericSuperclassTypeArguments(NonGenericSubclassOfList.class))
                .containsExactly(String.class);

        assertThat(TypeUtils.getGenericSuperclassTypeArguments(NonGenericSubclassOfMap.class))
                .containsExactly(String.class, Long.class);
    }

    private static class Bar extends Foo {}

    private static class Foo implements Generator<String> {
        @Override
        public String generate(final Random random) {
            return null;
        }
    }
}
