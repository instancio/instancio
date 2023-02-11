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

import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hint;
import org.instancio.generator.Hints;
import org.instancio.internal.ApiValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

/**
 * This hint is for generators that create collections.
 * <p>
 * The responsibility of a generator is to provide an instance of a collection
 * to the engine. The collection may be partially populated (with expected data).
 * The engine takes care of populating the collection with additional elements.
 *
 * <p><b>Sample use case</b></p>
 *
 * <p>The goal is to set up a collection with a couple of <i>expected</i> objects
 * and let the engine populate the rest of the collection with random data.</p>
 * <p>
 * This can be achieved as follows:
 * <p>
 * <pre>{@code
 * class PhonesGenerator implements Generator<List<Phone>> {
 *
 *     @Override
 *     public List<Phone> generate(Random random) {
 *         List<Phone> list = new ArrayList<>();
 *         list.add(Phone.builder().number("111-222-3333").phoneType(PhoneType.CELL).build());
 *         list.add(Phone.builder().number("111-444-5555").build()); // phoneType is null, but will be populated
 *         return list;
 *     }
 *
 *     @Override
 *     public Hints hints() {
 *         return Hints.builder()
 *                  // tells the engine to populate null fields in the objects created in the generate() method
 *                 .afterGenerate(AfterGenerate.POPULATE_NULLS)
 *                 .with(CollectionHint.builder()
 *                          // number of additional elements to be generated and added to the collection by the engine
 *                         .generateElements(3)
 *                         .shuffle(true) // shuffle the collection once populated
 *                         .build())
 *                 .build();
 *     }
 * }
 *
 * // Generator usage
 * Person person = Instancio.of(Person.class)
 *         .supply(field("phoneNumbers"), new PhonesGenerator())
 *         .create();
 *
 * // Expected results
 * assertThat(person.getAddress().getPhoneNumbers())
 *         .hasSize(5)
 *         .doesNotContainNull()
 *         .anyMatch(p -> p.getNumber().equals("111-222-3333") && p.getPhoneType() == PhoneType.CELL)
 *         .anyMatch(p -> p.getNumber().equals("111-444-5555") && p.getPhoneType() != null); // phoneType populated
 * }</pre>
 * <p>
 * Summary of results:
 * <ul>
 *   <li>The generated collection contains our custom objects as well as generated phone objects.</li>
 *   <li>The {@code phoneType} field of "111-444-5555" has been populated since
 *       the {@link AfterGenerate#POPULATE_NULLS} was specified.</li>
 *   <li>The collection has been shuffled since the {@link CollectionHint#shuffle()} was specified.</li>
 * </ul>
 *
 * @see Generator#hints()
 * @see Hints
 * @since 2.0.0
 */
public final class CollectionHint implements Hint<CollectionHint> {
    private static final CollectionHint EMPTY_HINT = builder().build();

    private int generateElements;
    private final boolean nullableElements;
    private final boolean shuffle;
    private final boolean unique;
    private final List<?> withElements;

    private CollectionHint(final Builder builder) {
        generateElements = builder.generateElements;
        nullableElements = builder.nullableElements;
        shuffle = builder.shuffle;
        unique = builder.unique;
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
    public static CollectionHint empty() {
        return EMPTY_HINT;
    }

    /**
     * Indicates the number of elements the engine should
     * generate and insert into the collection.
     *
     * @return number of elements to generate
     * @since 2.0.0
     */
    public int generateElements() {
        return generateElements;
    }

    /**
     * Sets number of elements to generate.
     *
     * @param generateElements number of elements to generate
     * @since 2.7.0
     */
    public void generateElements(final int generateElements) {
        this.generateElements = generateElements;
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
     * Indicates whether collection elements should be shuffled.
     *
     * @return {@code true} if elements should be shuffled, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean shuffle() {
        return shuffle;
    }

    public boolean unique() {
        return unique;
    }

    /**
     * Returns additional elements provided by the generator to the engine that are
     * to be inserted into the collection.
     *
     * @param <T> element type
     * @return specific elements to be inserted into a collection
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> withElements() {
        return (List<T>) withElements;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "CollectionHint[", "]")
                .add("generateElements=" + generateElements)
                .add("nullableElements=" + nullableElements)
                .add("shuffle=" + shuffle)
                .add("withElements=" + withElements)
                .toString();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int generateElements;
        private boolean nullableElements;
        private boolean shuffle;
        private boolean unique;
        private List<Object> withElements;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        /**
         * Indicates how many elements the engine should generate
         * and insert into the collection.
         *
         * @param generateElements number of elements to generate
         * @return builder instance
         * @since 2.0.0
         */
        public Builder generateElements(final int generateElements) {
            ApiValidator.isTrue(generateElements >= 0,
                    "Number of elements to generate must not be negative");
            this.generateElements = generateElements;
            return this;
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
         * Indicates whether collection elements, once populated, should be randomly shuffled.
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
         * Indicates that a collection containing unique elements should be generated.
         *
         * @param unique if {@code true}, unique elements will be unique
         * @return builder instance
         * @since 2.8.0
         */
        public Builder unique(final boolean unique) {
            this.unique = unique;
            return this;
        }

        /**
         * Additional elements to be inserted into the collection by the engine
         * during the population process. This method can be invoked more than once.
         *
         * @param elements elements to be inserted into the collection in addition
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
        public CollectionHint build() {
            return new CollectionHint(this);
        }
    }
}
