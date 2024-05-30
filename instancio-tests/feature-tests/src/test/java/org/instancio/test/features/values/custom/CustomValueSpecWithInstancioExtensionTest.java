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
package org.instancio.test.features.values.custom;

import org.instancio.generator.ValueSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test using a custom {@link ValueSpec} running under {@link InstancioExtension}.
 *
 * @see CustomValueSpecWithoutInstancioExtensionTest
 */
@FeatureTag(Feature.VALUE_SPEC)
@ExtendWith(InstancioExtension.class)
class CustomValueSpecWithInstancioExtensionTest {

    private static final Set<String> generatedValues = new HashSet<>();

    /**
     * Since this test uses the {@link InstancioExtension},
     * the spec must be instantiated within the {@code @Test} method
     * to have the thread-local random initialised.
     */
    private static CustomSpec spec() {
        return new CustomSpec();
    }

    @Nested
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WithSeedTest {

        @Seed(-1)
        @RepeatedTest(5)
        @Order(1)
        void first() { // NOSONAR
            generatedValues.add(spec().get());
        }

        @Test
        @Order(2)
        void shouldContainMultipleValues() {
            // The same value should have been generated
            assertThat(generatedValues).hasSize(1);
        }
    }

    @Test
    void withDefaultLength() {
        assertThat(spec().get()).hasSizeBetween(CustomSpec.DEFAULT_MIN_LENGTH, CustomSpec.DEFAULT_MAX_LENGTH);
    }

    @Test
    void length() {
        final int length = 3;
        assertThat(spec().length(length).get()).hasSize(length);
    }

}
