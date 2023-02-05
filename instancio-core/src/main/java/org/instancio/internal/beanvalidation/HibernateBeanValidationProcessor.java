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
package org.instancio.internal.beanvalidation;

import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;
import org.instancio.internal.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.util.Set;

final class HibernateBeanValidationProcessor extends AbstractBeanValidationProvider {

    private static final Set<Class<? extends Annotation>> PRIMARY_ANNOTATIONS =
            CollectionUtils.asSet(UUID.class, URL.class);

    private final HibernateBeanValidationHandlerResolver resolver =
            HibernateBeanValidationHandlerResolver.getInstance();

    @Override
    public AnnotationHandlerResolver getAnnotationHandlerResolver() {
        return resolver;
    }

    @Override
    public boolean isPrimary(final Class<? extends Annotation> annotationType) {
        return PRIMARY_ANNOTATIONS.contains(annotationType);
    }
}
