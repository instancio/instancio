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
package org.instancio.internal.util;

import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.exception.InstancioTerminatingException;
import org.instancio.generator.Generator;
import org.instancio.internal.generator.lang.StringGenerator;
import org.instancio.internal.generator.text.TextPatternGenerator;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.inheritance.NonGenericSubclassOfList;
import org.instancio.test.support.pojo.generics.inheritance.NonGenericSubclassOfMap;
import org.instancio.testsupport.fixtures.Types;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TypeUtilsTest {

    private static final Type TYPE_VARIABLE = Types.LIST_TYPE_VARIABLE.get();

    @Nested
    class GetRawTypeTest {
        @Test
        void withClass() {
            assertThat(TypeUtils.getRawType(String.class)).isEqualTo(String.class);
        }

        @Test
        void withParameterizedType() {
            assertThat(TypeUtils.getRawType(Types.LIST_ITEM_STRING.get())).isEqualTo(List.class);
        }

        @Test
        @SuppressWarnings("rawtypes")
        void withArrayOfParameterizedType() {
            List[] arrayOfLists = new List[0];
            assertThat(TypeUtils.getRawType(arrayOfLists.getClass())).isEqualTo(List[].class);
        }

        @Test
        void withTypeVariable() {
            assertThatThrownBy(() -> TypeUtils.getRawType(TYPE_VARIABLE))
                    .isExactlyInstanceOf(InstancioTerminatingException.class)
                    .hasMessageContaining("Unhandled type: %s", TYPE_VARIABLE.getClass().getSimpleName());
        }
    }

    @Test
    void getTypeParameterCount() {
        assertThat(TypeUtils.getTypeParameterCount(String.class)).isZero();
        assertThat(TypeUtils.getTypeParameterCount(List.class)).isOne();
        assertThat(TypeUtils.getTypeParameterCount(List[].class)).isOne();
    }

    @Test
    void getArrayClass() {
        assertThat(TypeUtils.getArrayClass(String.class)).isEqualTo(String[].class);
        assertThat(TypeUtils.getArrayClass(Types.LIST_STRING.get())).isEqualTo(List[].class);
        assertThat(TypeUtils.getArrayClass(Integer[].class)).isEqualTo(Integer[].class);
        assertThat(TypeUtils.getArrayClass(int[].class)).isEqualTo(int[].class);
        assertThat(TypeUtils.getArrayClass(new TypeToken<Item<String>[]>() {}.get())).isEqualTo(Item[].class);

        assertThatThrownBy(() -> TypeUtils.getArrayClass(TYPE_VARIABLE))
                .isExactlyInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not resolve array class for type: %s", TYPE_VARIABLE);
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
        public @Nullable String generate(final Random random) {
            return null;
        }
    }
}
