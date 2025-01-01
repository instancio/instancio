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
package org.instancio.spi.tests;

import org.example.spi.CustomTypeProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.spi.InstancioSpiException;
import org.instancio.test.support.pojo.basic.BooleanHolder;
import org.instancio.test.support.pojo.basic.LongHolder;
import org.instancio.test.support.pojo.interfaces.MultipleInterfaceImpls;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Tests for instantiator SPI {@link CustomTypeProvider#getTypeInstantiator()}.
 *
 * <p>For testing purposes, the instantiator returns static objects,
 * so that they can be asserted using {@code isSameAs()}.
 */
@ExtendWith(InstancioExtension.class)
class TypeInstantiatorTest {

    /**
     * Should overwrite the values set by the custom instantiator
     * because {@link Keys#OVERWRITE_EXISTING_VALUES} is true by default.
     */
    @Test
    void longHolder() {
        final LongHolder result = Instancio.create(LongHolder.class);

        assertThat(result).isSameAs(CustomTypeProvider.LONG_HOLDER);

        assertThat(result.getPrimitive()).isNotEqualTo(CustomTypeProvider.LONG_HOLDER_INITIAL_VALUE);
        assertThat(result.getWrapper()).isNotEqualTo(CustomTypeProvider.LONG_HOLDER_INITIAL_VALUE);
    }

    @Test
    void widgetInterface() {
        final MultipleInterfaceImpls.WidgetContainer result = Instancio.create(
                MultipleInterfaceImpls.WidgetContainer.class);

        assertThat(result.getWidget()).isSameAs(CustomTypeProvider.WIDGET);
        assertThat(result.getWidget().getWidgetName()).isNotBlank();
    }

    @Test
    void shouldThrowErrorIfWrongTypeReturnedByCustomInstantiator() {
        assertThatThrownBy(() -> Instancio.create(BooleanHolder.class))
                .isExactlyInstanceOf(InstancioSpiException.class)
                .hasMessage(String.format(
                        "%s instantiated an object of invalid type:%n" +
                                " -> class org.instancio.test.support.pojo.basic.CharacterHolder%n" +
                                "Expected an instance of:%n" +
                                " -> class org.instancio.test.support.pojo.basic.BooleanHolder",
                        CustomTypeProvider.class));
    }
}
