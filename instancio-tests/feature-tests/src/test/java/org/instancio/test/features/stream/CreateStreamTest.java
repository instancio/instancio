/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.test.features.stream;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag(Feature.STREAM)
@ExtendWith(InstancioExtension.class)
class CreateStreamTest {

    private static final int LIMIT = 100;

    /**
     * Tests for {@code Instancio.stream(...)} methods.
     */
    @Nested
    class InstancioStreamTest {
        @Test
        void streamClass() {
            final Stream<String> results = Instancio.stream(String.class).limit(LIMIT);
            assertThat(results).hasSize(LIMIT);
        }

        @Test
        void streamTypeToken() {
            final Stream<String> results = Instancio.stream(new TypeToken<String>() {}).limit(LIMIT);
            assertThat(results).hasSize(LIMIT);
        }

        @Test
        void streamModel() {
            final Model<Phone> model = Instancio.of(Phone.class)
                    .set(field(Phone::getCountryCode), "+1")
                    .generate(field(Phone::getNumber), gen -> gen.string().digits())
                    .toModel();

            // Method under test
            final Stream<Phone> result = Instancio.stream(model).limit(LIMIT);

            assertThat(result)
                    .hasSize(LIMIT)
                    .allSatisfy(phone -> {
                        assertThat(phone.getCountryCode()).isEqualTo("+1");
                        assertThat(phone.getNumber()).containsOnlyDigits();
                    });
        }
    }

    /**
     * Tests for {@code Instancio.of(...).stream()} methods.
     */
    @Nested
    class InstancioOfStreamTest {
        @Test
        void ofModelStream() {
            final Model<Phone> model = Instancio.of(Phone.class)
                    .set(field(Phone::getCountryCode), "+1")
                    .toModel();

            // Method under test
            final Stream<Phone> results = Instancio.of(model)
                    .generate(field(Phone::getNumber), gen -> gen.string().digits())
                    .stream()
                    .limit(LIMIT);

            assertThat(results)
                    .hasSize(LIMIT)
                    .allSatisfy(phone -> {
                        assertThat(phone.getCountryCode()).isEqualTo("+1");
                        assertThat(phone.getNumber()).containsOnlyDigits();
                    });
        }

        @Test
        void ofClassStream() {
            final int stringLength = 20;
            final Stream<String> results = Instancio.of(String.class)
                    .generate(allStrings(), gen -> gen.string().length(stringLength))
                    .stream()
                    .limit(LIMIT);

            assertThat(results)
                    .hasSize(LIMIT)
                    .allSatisfy(s -> assertThat(s).hasSize(stringLength));
        }

        @Test
        void ofTypeTokenStream() {
            final int stringLength = 20;

            final Stream<String> results = Instancio.of(new TypeToken<String>() {})
                    .generate(allStrings(), gen -> gen.string().length(stringLength))
                    .stream()
                    .limit(LIMIT);

            assertThat(results)
                    .hasSize(LIMIT)
                    .allSatisfy(s -> assertThat(s).hasSize(stringLength));
        }
    }

    @Test
    void withSeed() {
        final long seed = Instancio.create(long.class);

        // Should produce distinct UUIDs
        final Set<UUID> set1 = Instancio.of(UUID.class)
                .withSeed(seed)
                .stream()
                .limit(LIMIT)
                .collect(toSet());

        final Set<UUID> set2 = Instancio.of(UUID.class)
                .withSeed(seed)
                .stream()
                .limit(LIMIT)
                .collect(toSet());

        assertThat(set1).isEqualTo(set2).hasSize(LIMIT);
    }
}