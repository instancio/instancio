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
package org.instancio.internal.settings;

import org.instancio.generator.AfterGenerate;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.BeanValidationTarget;
import org.instancio.settings.Keys;
import org.instancio.settings.Mode;
import org.instancio.settings.OnSetFieldError;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.StringCase;
import org.instancio.settings.StringType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class SettingsSupportTest {

    @Test
    void verifyFunctions() {
        assertThat(SettingsSupport.getFunction(Boolean.class).apply("true")).isTrue();
        assertThat(SettingsSupport.getFunction(Byte.class).apply("5")).isEqualTo((byte) 5);
        assertThat(SettingsSupport.getFunction(Short.class).apply("6")).isEqualTo((short) 6);
        assertThat(SettingsSupport.getFunction(Integer.class).apply("7")).isEqualTo(7);
        assertThat(SettingsSupport.getFunction(Long.class).apply("8")).isEqualTo(8L);
        assertThat(SettingsSupport.getFunction(Float.class).apply("10.8")).isEqualTo(10.8f);
        assertThat(SettingsSupport.getFunction(Double.class).apply("10.2")).isEqualTo(10.2d);
    }

    @Test
    @SuppressWarnings("unchecked")
    <E extends Enum<E>> void verifyFunctionExistsForEveryEnumSetting() {
        final Class<E>[] enumClasses = new Class[]{
                AfterGenerate.class,
                AssignmentType.class,
                BeanValidationTarget.class,
                Mode.class,
                OnSetFieldError.class,
                OnSetMethodError.class,
                OnSetMethodNotFound.class,
                OnSetMethodUnmatched.class,
                SetterStyle.class,
                StringCase.class,
                StringType.class
        };

        for (Class<E> enumClass : enumClasses) {
            final E enumValue = ReflectionUtils.getEnumValues(enumClass)[0];
            final String enumName = enumValue.name();
            final Function<String, E> fn = SettingsSupport.getFunction(enumClass);

            assertThat(fn.apply(enumName)).isEqualTo(Enum.valueOf(enumClass, enumName));
        }

        final List<Class<E>> actualEnumClasses = getAllEnumKeyClasses();

        assertThat(actualEnumClasses)
                .as("'enumClasses' array should verify _all_ Keys of type Enum")
                .containsExactlyInAnyOrder(enumClasses);
    }

    @SuppressWarnings("unchecked")
    private static <E extends Enum<E>> List<Class<E>> getAllEnumKeyClasses() {
        return Keys.all().stream()
                .filter(k -> Enum.class.isAssignableFrom(k.type()))
                .map(objectSettingKey -> {
                    final Class<?> type = objectSettingKey.type();
                    return (Class<E>) type;
                })
                .toList();
    }
}
