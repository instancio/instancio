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
import org.instancio.TargetSelector;
import org.instancio.TypeToken;
import org.instancio.settings.Settings;
import org.instancio.spi.InstancioSpiException;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.ItemAlternativeImpl;
import org.instancio.test.support.pojo.generics.basic.ItemInterfaceHolder;
import org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;

class TypeResolverFromSpiTest {

    @Test
    @DisplayName("Should use class resolved via SPI")
    void shouldUseClassResolvedViaSPI() {
        final StringHolderInterface result = Instancio.create(StringHolderInterface.class);
        assertThat(result).isExactlyInstanceOf(StringHolderAlternativeImpl.class);
        assertThat(result.getValue()).isNotBlank();
    }

    /**
     * Subtype provided via {@link org.instancio.InstancioApi#subtype(TargetSelector, Class)}
     * takes precedence over the SPI.
     */
    @Test
    @DisplayName("Should use class specified via the API subtype() method")
    void shouldUseClassSpecifiedUsingSubtypeAPIMethod() {
        final StringHolderInterface result = Instancio.of(StringHolderInterface.class)
                .subtype(all(StringHolderInterface.class), StringHolder.class)
                .create();

        assertThat(result).isExactlyInstanceOf(StringHolder.class);
        assertThat(result.getValue()).isNotBlank();
    }

    /**
     * Subtype mapping specified via {@link Settings#mapType(Class, Class)}
     * takes precedence over the SPI.
     */
    @Test
    @DisplayName("Should use class specified via the API subtype() method")
    void shouldUseClassSpecifiedUsingSubtypeMappingFromSettings() {
        final Settings settings = Settings.create()
                .mapType(StringHolderInterface.class, StringHolder.class);

        final StringHolderInterface result = Instancio.of(StringHolderInterface.class)
                .withSettings(settings)
                .create();

        assertThat(result).isExactlyInstanceOf(StringHolder.class);
        assertThat(result.getValue()).isNotBlank();
    }

    @Test
    @DisplayName("Should use class resolved via SPI (with generic class)")
    void shouldUseClassResolvedViaSPIWithGenericClass() {
        final ItemInterfaceHolder<String> result = Instancio.create(new TypeToken<ItemInterfaceHolder<String>>() {});
        assertThat(result.getItemInterface()).isExactlyInstanceOf(ItemAlternativeImpl.class);
        assertThat(result.getItemInterface().getValue()).isNotBlank();
    }

    @Test
    void shouldThrowExceptionIfGivenInvalidSubtype() {
        assertThatThrownBy(() -> Instancio.create(BaseClassSubClassInheritance.SubClass.class))
                .isExactlyInstanceOf(InstancioSpiException.class)
                .hasMessage(String.format(
                        "%n%s provided an invalid subtype:%n" +
                                " -> class org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance$BaseClass%n" +
                                "is not a subtype of:%n" +
                                " -> class org.instancio.test.support.pojo.inheritance.BaseClassSubClassInheritance$SubClass",
                        CustomTypeProvider.class));
    }
}
