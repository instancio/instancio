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
package org.instancio.test.features.beanvalidation;

import org.instancio.Instancio;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;

/**
 * This module does not import Jakarta or Hibernate validator jars.
 * Enabling bean validation should not cause any errors.
 */
@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class BeanValidationEnabledWithNoProvidersTest {

    private static final String[] BEAN_VALIDATION_CLASSES = {
            "jakarta.validation.Validation",
            "org.hibernate.validator.HibernateValidator"
    };

    @Test
    void shouldIgnoreBeanValidationSettingIfNoProviderIsAvailable() {
        assertThat(BEAN_VALIDATION_CLASSES) // NOSONAR
                .as("Precondition: the classes must not be on the classpath")
                .allMatch(klass -> ReflectionUtils.loadClass(klass) == null);

        final Phone result = Instancio.of(Phone.class)
                .withSettings(Settings.create().set(Keys.BEAN_VALIDATION_ENABLED, true))
                .create();

        assertThatObject(result).hasNoNullFieldsOrProperties();
    }
}
