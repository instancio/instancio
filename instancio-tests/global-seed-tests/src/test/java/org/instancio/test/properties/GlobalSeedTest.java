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
package org.instancio.test.properties;

import org.instancio.Instancio;
import org.instancio.Result;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Seed precedence when NOT using {@code InstancioExtension}, from highest to lowest:
 *
 * <ul>
 *   <li>(a) withSeed()</li>
 *   <li>(b) withSettings()</li>
 *   <li>(c) Global seed (from properties file)</li>
 *   <li>(d) random seed, if none of the above specified (default behaviour, not tested here)</li>
 * </ul>
 */
@FeatureTag({Feature.GLOBAL_SEED, Feature.WITH_SEED})
// Do not use InstancioExtension at the top-level test class
class GlobalSeedTest {

    @Test
    @DisplayName("(a) withSeed() takes precedence over everything else")
    void withSeedTakesPrecedenceOverGlobalSeed() {
        final long seed = Instancio.create(long.class);
        final Result<String> s1 = Instancio.of(String.class).withSeed(seed).asResult();
        final Result<String> s2 = Instancio.of(String.class).withSeed(seed).asResult();
        final Result<String> s3 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(seed);
        assertThat(s3.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

        assertThat(s1.get())
                .as("Same value should be generated using given seed")
                .isEqualTo(s2.get());

        assertThat(s1.get()).isNotEqualTo(s3.get());
    }

    @Test
    @DisplayName("(b) withSettings()")
    void settingsSeedTakesPrecedenceOverGlobalSeed() {
        final long seed = Instancio.create(long.class);
        final Settings settings = Settings.create().set(Keys.SEED, seed);
        final Result<String> s1 = Instancio.of(String.class).withSettings(settings).asResult();
        final Result<String> s2 = Instancio.of(String.class).withSettings(settings).asResult();
        final Result<String> s3 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(seed);
        assertThat(s2.getSeed()).isEqualTo(seed);
        assertThat(s3.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

        assertThat(s1.get())
                .as("Same value should be generated using given seed")
                .isEqualTo(s2.get());

        assertThat(s1.get()).isNotEqualTo(s3.get());
    }

    @Test
    @DisplayName("(c) Global seed from properties file")
    void seedFromProperties() {
        final Result<String> s1 = Instancio.of(String.class).asResult();
        final Result<String> s2 = Instancio.of(String.class).asResult();

        assertThat(s1.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);
        assertThat(s2.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

        assertThat(s1.get())
                .as("Distinct values should be generated")
                .isNotEqualTo(s2.get());
    }

    @Test
    @DisplayName("(d) Global seed from properties file using Gen")
    void seedFromPropertiesUsingGen() {
        final String s1 = Instancio.gen().string().get();
        final String s2 = Instancio.gen().string().get();

        assertThat(s1)
                .as("Distinct values should be generated")
                .isNotEqualTo(s2);
    }

    /**
     * Without the extension, all test methods share the same instance
     * of random, therefore test methods produce different results.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class WithoutInstancioExtensionTest { // No InstancioExtension!

        private final Set<String> generatedValues = new HashSet<>();

        @Order(1)
        @Test
        void testA() {
            final Result<String> result = Instancio.of(String.class).asResult();
            assertThat(result.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

            generatedValues.add(result.get());
        }

        @Order(2)
        @Test
        void testB() {
            final Result<String> result = Instancio.of(String.class).asResult();
            assertThat(result.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

            generatedValues.add(result.get());
        }

        @Order(3)
        @Test
        void verify() {
            assertThat(generatedValues).hasSize(2);
        }
    }

    /**
     * With the extension, each test method should get its own instance
     * of random, therefore each method produces identical results.
     */
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @ExtendWith(InstancioExtension.class) // with extension
    class WithInstancioExtensionTest {

        private final Set<String> generatedValues = new HashSet<>();

        @Order(1)
        @Test
        void testA() {
            final Result<String> result = Instancio.of(String.class).asResult();
            assertThat(result.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

            generatedValues.add(result.get());
        }

        @Order(2)
        @Test
        void testB() {
            final Result<String> result = Instancio.of(String.class).asResult();
            assertThat(result.getSeed()).isEqualTo(TestConstants.GLOBAL_SEED);

            generatedValues.add(result.get());
        }

        @Order(3)
        @Test
        void verify() {
            assertThat(generatedValues).hasSize(1);
        }
    }
}
