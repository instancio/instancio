/*
 * Copyright 2022 the original author or authors.
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
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TypeUtilsTest {

    @Test
    void getArrayClass() {
        assertThat(TypeUtils.getArrayClass(new TypeToken<Integer[]>() {}.get())).isEqualTo(Integer[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<int[]>() {}.get())).isEqualTo(int[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<Item<Integer>[]>() {}.get())).isEqualTo(Item[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<Item<String>[]>() {}.get())).isEqualTo(Item[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<String>() {}.get())).isEqualTo(String[].class);
    }

    @Test
    void getGenericSuperclassRawTypeArgument() {
        assertThat(TypeUtils.getGeneratorTypeArgument(StringGenerator.class)).isEqualTo(String.class);
        assertThat(TypeUtils.getGeneratorTypeArgument(TextPatternGenerator.class)).isEqualTo(String.class);
        assertThat(TypeUtils.getGeneratorTypeArgument(Bar.class))
                .as("Should return String.class. Currently unsupported as it's needed at this time.")
                .isNull();
    }

    private static class Bar extends Foo {}

    private static class Foo implements Generator<String> {
        @Override
        public String generate(final Random random) {
            return null;
        }
    }
}
