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
package org.instancio.internal.annotation;

import org.instancio.internal.context.ModelContext;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AnnotationLibraries {

    private static final Logger LOG = LoggerFactory.getLogger(AnnotationLibraries.class);

    private static final String JAVAX_PERSISTENCE_CLASS = "javax.persistence.Column";
    private static final String JAKARTA_PERSISTENCE_CLASS = "jakarta.persistence.Column";
    private static final String JAVAX_VALIDATOR_CLASS = "javax.validation.Validation";
    private static final String JAKARTA_VALIDATOR_CLASS = "jakarta.validation.Validation";
    private static final String HIBERNATE_VALIDATOR_CLASS = "org.hibernate.validator.HibernateValidator";

    private AnnotationLibraries() {
        // non-instantiable
    }

    public static List<AnnotationLibraryFacade> discoverOnClasspath(final ModelContext modelContext) {
        final Settings settings = modelContext.getSettings();
        final boolean jpaEnabled = settings.get(Keys.JPA_ENABLED);
        final boolean beanValidationEnabled = settings.get(Keys.BEAN_VALIDATION_ENABLED);

        LOG.trace("Keys.BEAN_VALIDATION_ENABLED={}, Keys.JPA_ENABLED={}",
                beanValidationEnabled, jpaEnabled);

        // List order matters - later entry may override earlier one
        final List<AnnotationLibraryFacade> result = new ArrayList<>();

        // In theory, there shouldn't be javax and jakarta on the classpath
        // at the same time. In practice, it can happen, so we add both.

        if (jpaEnabled) {
            if (ReflectionUtils.loadClass(JAVAX_PERSISTENCE_CLASS) != null) {
                result.add(new JavaxPersistenceAnnotationLibraryFacade());
            }
            if (ReflectionUtils.loadClass(JAKARTA_PERSISTENCE_CLASS) != null) {
                result.add(new JakartaPersistenceAnnotationLibraryFacade());
            }
        }

        if (beanValidationEnabled) {
            if (ReflectionUtils.loadClass(HIBERNATE_VALIDATOR_CLASS) != null) {
                result.add(new HibernateBeanValidationAnnotationLibraryFacade());
            }
            if (ReflectionUtils.loadClass(JAKARTA_VALIDATOR_CLASS) != null) {
                result.add(new JakartaBeanValidationAnnotationLibraryFacade());
            }
            if (ReflectionUtils.loadClass(JAVAX_VALIDATOR_CLASS) != null) {
                result.add(new JavaxBeanValidationAnnotationLibraryFacade());
            }
        }
        return Collections.unmodifiableList(result);
    }
}
