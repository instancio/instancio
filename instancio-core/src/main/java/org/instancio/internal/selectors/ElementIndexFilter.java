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
package org.instancio.internal.selectors;

import org.instancio.documentation.InternalApi;
import org.instancio.internal.ApiValidator;

/**
 * Specifies which element indices within a list should be targeted.
 *
 * @since 6.0.0
 */
@InternalApi
public sealed interface ElementIndexFilter {

    boolean contains(int index, int listSize);

    int requiredMinSize();

    @SuppressWarnings("PMD.UseVarargs")
    static String join(final int[] indices) {
        final StringBuilder sb = new StringBuilder();
        for (int index : indices) {
            sb.append(index).append(", ");
        }
        sb.setLength(sb.length() - 2);
        return sb.toString();
    }

    record At(int index) implements ElementIndexFilter {

        public At {
            ApiValidator.isTrue(index >= 0, "index must not be negative: %s", index);
        }

        @Override
        public boolean contains(final int index, final int listSize) {
            return index == this.index;
        }

        @Override
        public int requiredMinSize() {
            return index + 1;
        }

        @Override
        public String toString() {
            return "at(" + index + ")";
        }
    }

    record MultipleIndices(int[] indices) implements ElementIndexFilter {

        public MultipleIndices {
            ApiValidator.isTrue(indices.length > 0, "'.at(indices)' must not be empty");
            for (int i : indices) {
                ApiValidator.isTrue(i >= 0, "index must not be negative: %s", i);
            }
        }

        @Override
        public boolean contains(final int index, final int listSize) {
            for (int i : indices) {
                if (i == index) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int requiredMinSize() {
            int max = 0;
            for (int i : indices) {
                if (i > max) {
                    max = i;
                }
            }
            return max + 1;
        }

        @Override
        public String toString() {
            return "at(" + join(indices) + ')';
        }
    }

    record Except(int[] excludedIndices) implements ElementIndexFilter {

        public Except {
            ApiValidator.isTrue(excludedIndices.length > 0, "'.except(indices)' must not be empty");
            for (int i : excludedIndices) {
                ApiValidator.isTrue(i >= 0, "index must not be negative: %s", i);
            }
        }

        @Override
        public boolean contains(final int index, final int listSize) {
            for (int i : excludedIndices) {
                if (i == index) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public int requiredMinSize() {
            return 0;
        }

        @Override
        public String toString() {
            return "except(" + join(excludedIndices) + ')';
        }
    }

    record Range(int startInclusive, int endInclusive) implements ElementIndexFilter {

        public Range {
            ApiValidator.isTrue(startInclusive >= 0, "index must not be negative: %s", startInclusive);
            ApiValidator.validateStartEnd(startInclusive, endInclusive);
        }

        @Override
        public boolean contains(final int index, final int listSize) {
            return index >= startInclusive && index <= endInclusive;
        }

        @Override
        public int requiredMinSize() {
            return endInclusive + 1;
        }

        @Override
        public String toString() {
            return "range(%s, %s)".formatted(startInclusive, endInclusive);
        }
    }

    record First() implements ElementIndexFilter {

        @Override
        public boolean contains(final int index, final int listSize) {
            return index == 0;
        }

        @Override
        public int requiredMinSize() {
            return 1;
        }

        @Override
        public String toString() {
            return "first()";
        }
    }

    record Last() implements ElementIndexFilter {

        @Override
        public boolean contains(final int index, final int listSize) {
            return index == listSize - 1;
        }

        @Override
        public int requiredMinSize() {
            return 1;
        }

        @Override
        public String toString() {
            return "last()";
        }
    }
}
