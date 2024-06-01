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
package org.instancio.test.features.withunique;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

@FeatureTag({
        Feature.WITH_UNIQUE,
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.SELECT_GROUP
})
@ExtendWith(InstancioExtension.class)
class WithUniqueSelectorTest {

    private static final int COLLECTION_SIZE = 10;

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.INTEGER_MIN, 1)
            .set(Keys.INTEGER_MAX, 10)
            .set(Keys.COLLECTION_MIN_SIZE, COLLECTION_SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, COLLECTION_SIZE);

    @Nested
    class UniqueWithListOfIntegerTest {
        @WithSettings
        private final Settings settings = SETTINGS;

        @Test
        void primitiveAnWrapperSelector() {
            final List<Integer> results = Instancio.ofList(Integer.class)
                    .withUnique(allInts())
                    .create();

            assertThat(results)
                    .hasSize(COLLECTION_SIZE)
                    .doesNotHaveDuplicates();
        }
    }

    /**
     * Tests using {@link IntegerHolder} which has two fields: an int and an Integer.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class UniqueWithIntegerHolderTest {

        @WithSettings
        private final Settings settings = SETTINGS;

        @Test
        void uniqueMethodPerField() {
            final List<IntegerHolder> results = Instancio.ofList(IntegerHolder.class)
                    // unique must be called per field
                    .withUnique(field(IntegerHolder::getPrimitive))
                    .withUnique(field(IntegerHolder::getWrapper))
                    .create();

            assertThat(results).hasSize(COLLECTION_SIZE);
            assertThat(results).extracting(IntegerHolder::getPrimitive).doesNotHaveDuplicates();
            assertThat(results).extracting(IntegerHolder::getWrapper).doesNotHaveDuplicates();
        }

        private Stream<Arguments> uniqueSelectorMatchingBothFields() {
            return Stream.of(
                    Arguments.of(allInts()),
                    Arguments.of(all(all(int.class), all(Integer.class))),
                    Arguments.of(fields())
            );
        }

        @MethodSource("uniqueSelectorMatchingBothFields")
        @ParameterizedTest
        void selectorGroup_shouldGenerateUniqueValuesAcrossBothFields(final TargetSelector selector) {
            final Set<Object> generatedValues = new HashSet<>();

            final List<IntegerHolder> results = Instancio.ofList(IntegerHolder.class)
                    .size(5) // 2 fields x 5 values
                    .withUnique(selector)
                    .onComplete(selector, generatedValues::add)
                    .create();

            assertThat(results).hasSize(5);
            assertThat(results).extracting(IntegerHolder::getPrimitive).doesNotHaveDuplicates();
            assertThat(results).extracting(IntegerHolder::getWrapper).doesNotHaveDuplicates();
            assertThat(generatedValues).hasSize(COLLECTION_SIZE);
        }

        @MethodSource("uniqueSelectorMatchingBothFields")
        @ParameterizedTest
        void selectorGroup_errorWhenInsufficientResults(final TargetSelector selector) {
            final InstancioApi<List<IntegerHolder>> api = Instancio.ofList(IntegerHolder.class)
                    .withUnique(selector);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("Generation was abandoned");
        }
    }
}
