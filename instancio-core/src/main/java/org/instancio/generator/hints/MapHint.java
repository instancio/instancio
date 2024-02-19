/*
 * Copyright 2022-2024 the original author or authors.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * This hint is for generators that create maps.
 *
 * <p>The responsibility of a generator is to provide an instance of a map
 * to the engine. The map may be partially populated (with expected data).
 * The engine takes care of populating the map with additional entries.
 *
 * <p><b>Sample use case</b></p>
 *
 * <p>The goal is to set up a map with a couple of <i>expected</i> objects
 * and let the engine populate the rest of the map with random data.</p>
 *
 * <p>This can be achieved as follows:
 *
 * <pre>{@code
 * class PhonesGenerator implements Generator<Map<Long, Phone>> {
 *
 *     @Override
 *     public Map<Long, Phone> generate(Random random) {
 *         Map<Long, Phone> map = new HashMap<>();
 *         map.put(1L, Phone.builder().number("111-222-3333").phoneType(PhoneType.CELL).build());
 *         map.put(2L, Phone.builder().number("111-444-5555").build()); // phoneType is null, but will be populated
 *         return map;
 *     }
 *
 *     @Override
 *     public Hints hints() {
 *         return Hints.builder()
 *                  // tells the engine to populate null fields in the objects created in the generate() method
 *                 .afterGenerate(AfterGenerate.POPULATE_NULLS)
 *                 .with(MapHint.builder()
 *                          // number of additional entries to be generated and added to the map by the engine
 *                         .generateEntries(3)
 *                         .build())
 *                 .build();
 *     }
 * }
 *
 * // Generator usage
 * Person person = Instancio.of(Person.class)
 *         .supply(field("phones"), new PhonesGenerator())
 *         .create();
 *
 * Map<Long, Phone> map = person.getPhones();
 *
 * // Expected results:
 * assertThat(map).hasSize(5);
 *
 * assertThat(map).extractingByKey(1L)
 *         .matches(p -> p.getNumber().equals("111-222-3333") && p.getPhoneType() == PhoneType.CELL);
 *
 * assertThat(map).extractingByKey(2L)
 *         .matches(p -> p.getNumber().equals("111-444-5555") && p.getPhoneType() != null);
 * }</pre>
 *
 * <p>Summary of results:
 * <ul>
 *   <li>The generated map contains our custom objects as well as generated phone objects.</li>
 *   <li>The {@code phoneType} field of "111-444-5555" has been populated since
 *       the {@link AfterGenerate#POPULATE_NULLS} was specified.</li>
 * </ul>
 *
 * @see Generator#hints()
 * @see Hints
 * @since 2.0.0
 */
public final class MapHint implements Hint<MapHint> {
    private static final MapHint EMPTY_HINT = builder().build();

    private final int generateEntries;
    private final boolean nullableMapKeys;
    private final boolean nullableMapValues;
    private final Map<?, ?> withEntries;
    private final List<?> withKeys;

    private MapHint(final Builder builder) {
        generateEntries = builder.generateEntries;
        nullableMapKeys = builder.nullableMapKeys;
        nullableMapValues = builder.nullableMapValues;
        withEntries = builder.withEntries == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(builder.withEntries);
        withKeys = builder.withKeys == null
                ? Collections.emptyList()
                : Collections.unmodifiableList(builder.withKeys);
    }

    /**
     * Returns an empty hint containing default values.
     *
     * @return empty hint
     * @since 2.0.0
     */
    public static MapHint empty() {
        return EMPTY_HINT;
    }

    /**
     * Indicates the number of entries the engine should
     * generate and insert into the map.
     *
     * @return number of entries to generate
     * @since 2.0.0
     */
    public int generateEntries() {
        return generateEntries;
    }

    /**
     * Indicates whether map keys can be {@code null}.
     *
     * @return {@code true} if keys are nullable, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean nullableMapKeys() {
        return nullableMapKeys;
    }

    /**
     * Indicates whether map values can be {@code null}.
     *
     * @return {@code true} if values are nullable, {@code false} otherwise
     * @since 2.0.0
     */
    public boolean nullableMapValues() {
        return nullableMapValues;
    }

    /**
     * Returns additional entries provided by the generator to the engine that are
     * to be inserted into the map.
     *
     * @param <K> key type
     * @param <V> value type
     * @return specific entries to be inserted into the map
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, V> withEntries() {
        return (Map<K, V>) withEntries;
    }

    /**
     * Returns keys provided by the generator to the engine that are
     * to be inserted into the map.
     *
     * @param <K> key type
     * @return keys to be inserted into the map
     * @since 2.0.0
     */
    @SuppressWarnings("unchecked")
    public <K> List<K> withKeys() {
        return (List<K>) withKeys;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", "MapHint[", "]")
                .add("generateEntries=" + generateEntries)
                .add("nullableMapKeys=" + nullableMapKeys)
                .add("nullableMapValues=" + nullableMapValues)
                .add("withEntries=" + withEntries)
                .toString();
    }

    /**
     * Returns a builder for this hint type.
     *
     * @return hint builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for this hint.
     */
    public static final class Builder {
        private int generateEntries;
        private boolean nullableMapKeys;
        private boolean nullableMapValues;
        private Map<Object, Object> withEntries;
        private List<Object> withKeys;

        private Builder() {
        }

        /**
         * Indicates how many entries the engine should generate
         * and insert into the map.
         *
         * @param generateEntries size of the map
         * @return builder instance
         * @since 2.0.0
         */
        public Builder generateEntries(final int generateEntries) {
            ApiValidator.isTrue(generateEntries >= 0,
                    "Data structure size must not be negative");
            this.generateEntries = generateEntries;
            return this;
        }

        /**
         * Indicates whether {@code null}s are allowed to be inserted
         * as map keys.
         *
         * @param nullableMapKeys {@code null} keys allowed if {@code true}
         * @return builder instance
         * @since 2.0.0
         */
        public Builder nullableMapKeys(final boolean nullableMapKeys) {
            this.nullableMapKeys = nullableMapKeys;
            return this;
        }

        /**
         * Indicates whether {@code null}s are allowed to be inserted as map values.
         *
         * @param nullableMapValues {@code null} values allowed if {@code true}
         * @return builder instance
         * @since 2.0.0
         */
        public Builder nullableMapValues(final boolean nullableMapValues) {
            this.nullableMapValues = nullableMapValues;
            return this;
        }

        /**
         * The specified entries will be inserted into the map by the engine
         * during the population process. This method can be invoked more than once.
         *
         * @param entries to add
         * @param <K>     key type
         * @param <V>     value type
         * @return builder instance
         * @since 2.0.0
         */
        public <K, V> Builder withEntries(final Map<? extends K, ? extends V> entries) {
            if (entries == null) {
                return this;
            }
            if (withEntries == null) {
                withEntries = new HashMap<>();
            }
            this.withEntries.putAll(entries);
            return this;
        }

        /**
         * The specified keys will be inserted into the map by the engine
         * during the population process. This method can be invoked more than once.
         *
         * @param keys to add
         * @param <K>  key type
         * @return builder instance
         * @since 2.0.0
         */
        public <K> Builder withKeys(final List<? extends K> keys) {
            if (keys == null) {
                return this;
            }
            if (withKeys == null) {
                withKeys = new ArrayList<>();
            }
            this.withKeys.addAll(keys);
            return this;
        }

        /**
         * Builds the object.
         *
         * @return the built instance.
         */
        public MapHint build() {
            return new MapHint(this);
        }
    }
}
