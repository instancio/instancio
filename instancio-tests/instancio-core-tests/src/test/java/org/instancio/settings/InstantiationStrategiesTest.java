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
package org.instancio.settings;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.settings.InternalInstantiationStrategies;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.settings.InstantiationStrategy.ALL_ARGS;
import static org.instancio.settings.InstantiationStrategy.BYPASS_CONSTRUCTOR;
import static org.instancio.settings.InstantiationStrategy.NO_ARGS;

class InstantiationStrategiesTest {

    private static final String PROPERTY_KEY = "instantiation.strategies";

    private static InstantiationStrategies fromProperty(final String value) {
        return Settings.from(Map.of(PROPERTY_KEY, value))
                .get(Keys.INSTANTIATION_STRATEGIES);
    }

    @Nested
    class OfTest {

        @Test
        void retainsOrder() {
            assertThat(InstantiationStrategies.of(BYPASS_CONSTRUCTOR, NO_ARGS, ALL_ARGS))
                    .hasToString("BYPASS_CONSTRUCTOR,NO_ARGS,ALL_ARGS");
        }

        @Test
        void emptyIsRejected() {
            assertThatThrownBy(InstantiationStrategies::of)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("at least one instantiation strategy must be specified");
        }

        @Test
        void nullStrategyIsRejected() {
            final InstantiationStrategy[] containsNull = {NO_ARGS, null};

            assertThatThrownBy(() -> InstantiationStrategies.of(containsNull))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("instantiation strategy must not be null");
        }

        @Test
        void duplicateIsRejected() {
            assertThatThrownBy(() -> InstantiationStrategies.of(NO_ARGS, ALL_ARGS, NO_ARGS))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("duplicate instantiation strategy");
        }
    }

    @Nested
    class PropertyValueTest {

        @Test
        void retainsOrder() {
            assertThat(fromProperty("BYPASS_CONSTRUCTOR,NO_ARGS,ALL_ARGS"))
                    .isEqualTo(InstantiationStrategies.of(BYPASS_CONSTRUCTOR, NO_ARGS, ALL_ARGS));
        }

        @Test
        void ignoresSurroundingWhitespaceAndCase() {
            assertThat(fromProperty("  no_args , All_Args "))
                    .isEqualTo(InstantiationStrategies.of(NO_ARGS, ALL_ARGS));
        }

        @ParameterizedTest
        @ValueSource(strings = {"", " ", "\t"})
        void blankIsRejected(final String value) {
            assertThatThrownBy(() -> fromProperty(value))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("at least one instantiation strategy must be specified");
        }

        @Test
        void unknownStrategyIsRejected() {
            assertThatThrownBy(() -> fromProperty("NO_ARGS,BOGUS"))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("invalid instantiation strategy 'BOGUS'")
                    .hasMessageContaining("Valid values are:");
        }

        @Test
        void duplicateIsRejected() {
            assertThatThrownBy(() -> fromProperty("NO_ARGS,NO_ARGS"))
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("duplicate instantiation strategy");
        }

        /**
         * The value rendered into {@code instancio.properties}
         * must be accepted back by the property converter.
         */
        @Test
        void propertyFormatRoundTrips() {
            final InstantiationStrategies original =
                    InstantiationStrategies.of(NO_ARGS, ALL_ARGS, BYPASS_CONSTRUCTOR);

            assertThat(fromProperty(original.toString())).isEqualTo(original);
        }
    }

    /**
     * The value is compared when settings are merged or overridden,
     * so equality must be by content and order.
     */
    @Nested
    class ValueSemanticsTest {

        @Test
        void verifyEqualsAndHashcode() {
            EqualsVerifier.forClass(InternalInstantiationStrategies.class).verify();
        }

        @Test
        void orderIsSignificant() {
            assertThat(InstantiationStrategies.of(NO_ARGS, ALL_ARGS))
                    .isNotEqualTo(InstantiationStrategies.of(ALL_ARGS, NO_ARGS));
        }

        @Test
        void settingReturnsTheValueItWasGiven() {
            final InstantiationStrategies strategies = InstantiationStrategies.of(NO_ARGS, ALL_ARGS);
            final Settings settings = Settings.create().set(Keys.INSTANTIATION_STRATEGIES, strategies);

            assertThat(settings.get(Keys.INSTANTIATION_STRATEGIES))
                    .isEqualTo(strategies)
                    .isEqualTo(fromProperty("NO_ARGS,ALL_ARGS"));
        }
    }
}
