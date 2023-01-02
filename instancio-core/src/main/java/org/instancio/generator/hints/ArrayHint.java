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
package org.instancio.generator.hints;

import org.instancio.generator.Generator;
import org.instancio.generator.Hint;
import org.instancio.generator.Hints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * This hint is for generators that create arrays.
 * <p>
 * The responsibility of a generator is to provide an instance of an array
 * to the engine. The array may be partially populated (with expected data).
 * The engine takes care of populating the uninitialised indices.
 *
 * <p><b>Sample use case</b></p>
 *
 * <p>The goal is to set up an array with a couple of <i>expected</i> objects
 * and let the engine populate the rest of the array with random data.
 * Once populated, the expected elements the array should be shuffled
 * to randomise the position of expected elements.</p>
 * <p>
 * This can be achieved as follows:
 *
 * <pre>{@code
 *  class PhonesGenerator implements Generator<Phone[]> {
 *
 *     @Override
 *     public Phone[] generate(Random random) {
 *         int length = random.intRange(5, 10);
 *         Phone[] array = new Phone[length];
 *         array[0] = Phone.builder().number("111-222-3333").phoneType(PhoneType.CELL).build();
 *         array[1] = Phone.builder().number("111-444-5555").build(); // phoneType is null, but will be populated
 *         return array;
 *     }
 *
 *     @Override
 *     public Hints hints() {
 *         return Hints.builder()
 *                 .with(ArrayHint.builder()
 *                         .shuffle(true) // shuffle the array once populated
 *                         .build())
 *                 .build();
 *     }
 *  }
 *
 *  // Generator usage
 *  Person person = Instancio.of(Person.class)
 *         .supply(all(Phone[].class), new PhonesGenerator())
 *         .create();
 *
 *  // Expected results
 *  assertThat(person.getPhones())
 *          .hasSizeBetween(5, 10)
 *          .doesNotContainNull()
 *          .anyMatch(p -> p.getNumber().equals("111-222-3333") && p.getPhoneType() == PhoneType.CELL)
 *          .anyMatch(p -> p.getNumber().equals("111-444-5555") && p.getPhoneType() != null); // phoneType populated
 * }</pre>
 * <p>
 * Summary of results:
 * <ul>
 *   <li>The generated array contains our custom objects as well as generated phone objects.</li>
 *   <li>The {@code phoneType} field of "111-444-5555" has been populated.</li>
 *   <li>The array has been shuffled since the {@link ArrayHint#shuffle()} was specified.</li>
 * </ul>
 *
 * @see Generator#hints()
 * @see Hints
 * @since 2.0.0
 */
public final class ArrayHint implements Hint<ArrayHint> {
    private static final ArrayHint EMPTY_HINT = builder().build();

    private final boolean nullableElements;
    private final boolean shuffle;
    private final List<?> withElements;

    private ArrayHint(final Builder builder) {
        nullableElements = builder.nullableElements;
        shuffle = builder.shuffle;
        withElements = builder.withElements == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(builder.withElements);
    }

    /**
     * Returns an empty hint containing default values.
     *
     * @return empty hint
     * @since 2.0.0
     */
    public static ArrayHint empty() {
        return EMPTY_HINT;
    }

    /**
     * Indicates whether elements can be {@code null}.
     *
     * @return {@code true} if elements are nullable, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean nullableElements() {
        return nullableElements;
    }

    /**
     * Indicates whether array elements should be randomly shuffled.
     *
     * @return {@code true} if elements should be shuffled, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean shuffle() {
        return shuffle;
    }

    /**
     * Returns elements provided by the generator to the engine that are
     * to be inserted into the array.
     *
     * @param <T> element type
     * @return specific elements to be inserted into an array
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> withElements() {
        return (List<T>) withElements;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "ArrayHint[", "]")
                .add("nullableElements=" + nullableElements)
                .add("shuffle=" + shuffle)
                .add("withElements=" + withElements)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean nullableElements;
        private boolean shuffle;
        private List<Object> withElements;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Indicates whether {@code null} elements are allowed to be generated.
         *
         * @param nullableElements if {@code true}, {@code null} elements allowed
         * @return builder instance
         * @since 2.0.0
         */
        public Builder nullableElements(final boolean nullableElements) {
            this.nullableElements = nullableElements;
            return this;
        }

        /**
         * Indicates whether array elements, once populated, should be randomly shuffled.
         * The shuffle implementation ensures that results are reproducible per given seed.
         *
         * @param shuffle if {@code true}, elements will be shuffled
         * @return builder instance
         * @since 2.0.0
         */
        public Builder shuffle(final boolean shuffle) {
            this.shuffle = shuffle;
            return this;
        }

        /**
         * Additional elements to be inserted into the array by the engine
         * during the population process. This method can be invoked more than once.
         *
         * @param elements elements to be inserted into the array in addition
         *                 to the random elements generated by the engine
         * @param <T>      element type
         * @return builder instance
         * @since 2.0.0
         */
        public <T> Builder withElements(final List<? extends T> elements) {
            if (elements == null) {
                return this;
            }
            if (withElements == null) {
                withElements = new ArrayList<>();
            }
            this.withElements.addAll(elements);
            return this;
        }

        /**
         * Builds the object.
         *
         * @return the built instance.
         */
        public ArrayHint build() {
            return new ArrayHint(this);
        }
    }
}
