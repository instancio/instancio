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

import jakarta.validation.constraints.Email;
import org.instancio.exception.InstancioException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.domain.internet.EmailGenerator;

import java.lang.annotation.Annotation;

final class JakartaBeanValidationProcessor extends AbstractBeanValidationProvider {

    private final JakartaBeanValidationHandlerResolver resolver =
            JakartaBeanValidationHandlerResolver.getInstance();

    @Override
    public boolean isPrimary(final Class<? extends Annotation> annotationType) {
        return annotationType == Email.class;
    }

    @Override
    protected Generator<?> resolveGenerator(
            final Annotation annotation,
            final GeneratorContext context) {

        final Class<?> annotationType = annotation.annotationType();
        if (annotationType == Email.class) {
            return new EmailGenerator(context);
        }
        throw new InstancioException("Unmapped primary annotation:  " + annotationType.getName());
    }

    @Override
    protected AnnotationHandlerResolver getAnnotationHandlerResolver() {
        return resolver;
    }
}
