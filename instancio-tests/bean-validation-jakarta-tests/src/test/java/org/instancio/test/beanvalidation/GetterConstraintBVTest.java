/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.beanvalidation;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.BeanValidationTarget;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.pojo.beanvalidation.GetterConstraintBV;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GetterConstraintBVTest {

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.BEAN_VALIDATION_TARGET, BeanValidationTarget.GETTER);

    @Test
    void stringWithGetPrefix() {
        final GetterConstraintBV.StringWithGetPrefix result = Instancio.create(GetterConstraintBV.StringWithGetPrefix.class);

        assertThat(result.getDigits()).containsOnlyDigits();
    }

    @Test
    void stringWithNoPrefix() {
        final GetterConstraintBV.StringWithNoPrefix result = Instancio.create(GetterConstraintBV.StringWithNoPrefix.class);

        assertThat(result.digits()).containsOnlyDigits();
    }

    @Test
    void booleanWithGetPrefix() {
        final GetterConstraintBV.BooleanWithGetPrefix result = Instancio.create(GetterConstraintBV.BooleanWithGetPrefix.class);

        assertThat(result.getPrimitiveBoolean()).isTrue();
        assertThat(result.getBooleanWrapper()).isTrue();
    }

    @Test
    void booleanWithIsPrefix() {
        final GetterConstraintBV.BooleanWithIsPrefix result = Instancio.create(GetterConstraintBV.BooleanWithIsPrefix.class);

        assertThat(result.isPrimitiveBoolean()).isTrue();
        assertThat(result.isBooleanWrapper()).isTrue();
    }

    @Test
    void booleanWithNoPrefix() {
        final GetterConstraintBV.BooleanWithNoPrefix result = Instancio.create(GetterConstraintBV.BooleanWithNoPrefix.class);

        assertThat(result.primitiveBoolean()).isTrue();
        assertThat(result.booleanWrapper()).isTrue();
    }
}
