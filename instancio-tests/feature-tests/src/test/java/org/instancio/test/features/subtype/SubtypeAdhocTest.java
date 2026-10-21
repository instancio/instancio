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
package org.instancio.test.features.subtype;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.interfaces.ItemInterface;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.Serializable;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

/**
 * Tests targeting edge cases with subtypes.
 */
@SuppressWarnings("NullAway")
@FeatureTag(Feature.SUBTYPE)
@ExtendWith(InstancioExtension.class)
class SubtypeAdhocTest {

    private static class ItemHolder {
        ItemInterface<String> item;
    }

    /**
     * Subtype with <b>fewer</b> type params than the source.
     * Source: {@code Pair<A, B>} (2 params); {@code SubPair<X>} (1 param).
     */
    private static class SubPair<X> extends Pair<X, String> {
        // X maps to left; right is always String via getGenericSuperclass()
    }

    /**
     * Subtype with <b>more</b> type params than the source interface.
     * {@code ItemInterface<T>} has 1 param; {@code ItemWithLabel<T, LABEL>} has two.
     */
    private static @Data class ItemWithLabel<T, LABEL> implements ItemInterface<T> {
        private T value;
        private LABEL label;
    }

    /**
     * Subtype that implements a raw (non-generic) interface in addition to the
     * generic source interface. The raw interface is declared first so that
     * {@code getGenericInterfaces()} iterates over a non-{@code ParameterizedType}.
     */
    private static @Data class ItemWithLabelAndMarker<T, LABEL> implements Serializable, ItemInterface<T> {
        private T value;
        private LABEL label;
    }

    /**
     * Non-generic subtype that binds the source type parameter to a concrete type.
     * Source: {@code ItemInterface<T>} (1 param); target has 0 params.
     */
    private static @Data class ConcreteItem implements ItemInterface<String> {
        private String value;
    }

    @Nested
    class TargetHasMoreTypeParams {

        /**
         * Field {@code T} in subtype should resolve when subtype has extra type param
         */
        @Test
        void valueShouldBeResolved() {
            final ItemHolder result = Instancio.of(ItemHolder.class)
                    .subtype(all(ItemInterface.class), ItemWithLabel.class)
                    .create();

            assertThat(result.item).isInstanceOf(ItemWithLabel.class);

            final ItemWithLabel<?, ?> itemWithLabel = (ItemWithLabel<?, ?>) result.item;
            assertThat(itemWithLabel.getValue())
                    .as("T should resolve to String via the subtype bridge")
                    .isNotNull()
                    .isInstanceOf(String.class);
        }

        /**
         * {@code T} value in {@code ItemWithLabel} should be non-blank String when used as collection element
         */
        @Test
        void elementValueShouldBeResolved() {
            class ItemListHolder {
                List<ItemInterface<String>> items;
            }

            final ItemListHolder result = Instancio.of(ItemListHolder.class)
                    .subtype(all(ItemInterface.class), ItemWithLabel.class)
                    .create();

            assertThat(result.items).isNotEmpty().allSatisfy(item -> {
                assertThat(item).isInstanceOf(ItemWithLabel.class);

                assertThat(((ItemWithLabel<?, ?>) item).getValue())
                        .isNotNull()
                        .isInstanceOf(String.class);
            });
        }
    }

    @Nested
    class TargetWithRawInterface {

        /**
         * Field {@code T} in subtype should resolve when the subtype also
         * implements a raw (non-generic) interface before the generic one.
         */
        @Test
        void valueShouldBeResolved() {
            final ItemHolder result = Instancio.of(ItemHolder.class)
                    .subtype(all(ItemInterface.class), ItemWithLabelAndMarker.class)
                    .create();

            assertThat(result.item).isInstanceOf(ItemWithLabelAndMarker.class);

            final ItemWithLabelAndMarker<?, ?> item = (ItemWithLabelAndMarker<?, ?>) result.item;
            assertThat(item.getValue())
                    .as("T should resolve to String via the subtype bridge")
                    .isNotNull()
                    .isInstanceOf(String.class);
        }
    }

    @Nested
    class TargetWithConcreteTypeArg {

        /**
         * Value should resolve when the subtype binds the source type
         * parameter to a concrete type instead of a type variable.
         */
        @Test
        void valueShouldBeResolved() {
            final ItemHolder result = Instancio.of(ItemHolder.class)
                    .subtype(all(ItemInterface.class), ConcreteItem.class)
                    .create();

            assertThat(result.item).isInstanceOf(ConcreteItem.class);
            assertThat(result.item.getValue())
                    .as("value should be populated as a concrete String")
                    .isNotNull()
                    .isInstanceOf(String.class);
        }
    }

    @Nested
    class SubtypeWithSuperclassBinding {

        /**
         * Bridge type map: {@code SubPair<X> extends Pair<X, String>}.
         *
         * <p>Subtype mapping {@code Pair<Integer,Long> -> SubPair}:
         * explicit Long wins over SubPair's String binding
         */
        @Test
        void explicitTypeTokenWinsOverConcreteSubclassBinding() {
            final Pair<?, ?> result = Instancio.of(new TypeToken<Pair<Integer, Long>>() {})
                    .subtype(all(Pair.class), SubPair.class)
                    .create();

            assertThat(result).isInstanceOf(SubPair.class);

            assertThat(result.getLeft())
                    .as("left: Integer comes from Pair<Integer, Long>")
                    .isNotNull()
                    .isInstanceOf(Integer.class);

            assertThat(result.getRight())
                    .as("right: Long from the type token wins over String from SubPair's extends clause")
                    .isNotNull()
                    .isInstanceOf(Long.class);
        }
    }
}
