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
package org.instancio.spi.tests;

import org.example.FooRecord;
import org.example.NonMatchingSetterPojo;
import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.MethodModifier;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;

/**
 * see {@link org.example.spi.CustomSetterMethodResolver}
 */
@ExtendWith(InstancioExtension.class)
class SetterMethodResolverSpiTest {

    private static class PojoA {
        private Set<Long> _values;
        private boolean viaSetter_values;

        @SuppressWarnings("unused") // used via reflection
        private void setValues(final Collection<Long> c) {
            // field starts with '_' and should be resolved
            // by the custom method resolver
            _values = new HashSet<>(c);
            viaSetter_values = true;
        }
    }

    private static class PojoB {
        // field does not start with '_' and should by resolved
        // by built-in resolver, which is used as a fallback
        private long value;
        private boolean viaSetter_value;

        @SuppressWarnings("unused") // used via reflection
        private void withValue(final long value) {
            this.value = value;
            viaSetter_value = true;
        }
    }

    private static <T> Model<T> createModel(final Class<T> klass) {
        return Instancio.of(klass)
                // These are used for verification; we don't want to generate values for these flags
                .ignore(fields().matching("viaSetter.*"))
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                        .lock())
                .toModel();
    }

    @Test
    void shouldUseCustomSetterMethodResolver() {
        final PojoA result = Instancio.create(createModel(PojoA.class));

        assertThat(result._values).isNotEmpty();
        assertThat(result.viaSetter_values).isTrue();
    }

    @Test
    void shouldNotUserPrivateSetterMethodWhenExcluded() {
        final PojoA result = Instancio.of(createModel(PojoA.class))
                .withSettings(Settings.create()
                        .set(Keys.SETTER_EXCLUDE_MODIFIER, MethodModifier.PRIVATE))
                .create();

        assertThat(result._values).isNull();
        assertThat(result.viaSetter_values).isFalse();
    }

    @Test
    void shouldFallbackToBuiltInMethodResolver() {
        final PojoB result = Instancio.of(createModel(PojoB.class))
                .withSettings(Settings.create()
                        .set(Keys.SETTER_STYLE, SetterStyle.WITH))
                .create();

        assertThat(result.value).isPositive();
        assertThat(result.viaSetter_value).isTrue();

    }

    /**
     * {@link NonMatchingSetterPojo#nonMatchingSetter(String)} is unmatched
     * as it doesn't match the field name. Since {@link Keys#ON_SET_METHOD_UNMATCHED}
     * is set to true and {@link Keys#ON_SET_METHOD_NOT_FOUND} is set to FAIL,
     * without the SPI, this test would fail.
     */
    @Test
    void nonMatchingSetter() {
        final NonMatchingSetterPojo result = Instancio.of(NonMatchingSetterPojo.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                        .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.IGNORE)
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL))
                .create();

        assertThat(result.getValue()).isNotBlank();
    }

    @Test
    void shouldNotResolveSettersForRecords() {
        final FooRecord result = Instancio.of(FooRecord.class)
                .withSettings(Settings.create()
                        .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
                        .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.IGNORE)
                        .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.FAIL))
                .create();

        assertThat(result.value()).isNotBlank();
    }
}
