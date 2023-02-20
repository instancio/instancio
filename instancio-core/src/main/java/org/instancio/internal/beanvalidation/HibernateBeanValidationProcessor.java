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

import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.EAN;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;
import org.instancio.exception.InstancioException;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.domain.finance.CreditCardNumberGenerator;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.net.URLGenerator;
import org.instancio.internal.generator.text.LuhnGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;
import org.instancio.internal.util.StringUtils;

import java.lang.annotation.Annotation;

final class HibernateBeanValidationProcessor extends AbstractBeanValidationProvider {

    private final HibernateBeanValidationHandlerResolver resolver =
            HibernateBeanValidationHandlerResolver.getInstance();

    @Override
    public boolean isPrimary(final Class<? extends Annotation> annotationType) {
        return annotationType == EAN.class
                || annotationType == CreditCardNumber.class
                || annotationType == LuhnCheck.class
                || annotationType == URL.class
                || annotationType == UUID.class;
    }

    @Override
    protected Generator<?> resolveGenerator(
            final Annotation annotation,
            final GeneratorContext context) {

        final Class<?> annotationType = annotation.annotationType();
        if (annotationType == EAN.class) {
            final EAN ean = (EAN) annotation;

            final EanGenerator generator = new EanGenerator(context);
            if (ean.type() == EAN.Type.EAN8) {
                generator.ean8();
            }
            return generator;
        }
        if (annotationType == LuhnCheck.class) {
            final LuhnCheck luhn = (LuhnCheck) annotation;

            final LuhnGenerator generator = new LuhnGenerator(context)
                    .startIndex(luhn.startIndex())
                    .endIndex(luhn.endIndex());

            if (luhn.checkDigitIndex() != -1) {
                generator.checkIndex(luhn.checkDigitIndex());
            }
            return generator;
        }
        if (annotationType == CreditCardNumber.class) {
            return new CreditCardNumberGenerator(context);
        }
        if (annotationType == UUID.class) {
            return new UUIDGenerator(context);
        }
        if (annotationType == URL.class) {
            final URL url = (URL) annotation;
            final URLGenerator urlGenerator = new URLGenerator(context)
                    .port(url.port());

            if (!StringUtils.isBlank(url.protocol())) {
                urlGenerator.protocol(url.protocol());
            }
            if (!StringUtils.isBlank(url.host())) {
                urlGenerator.host(random -> url.host());
            }
            return urlGenerator;
        }
        throw new InstancioException("Unmapped primary annotation:  " + annotationType.getName());
    }

    @Override
    protected AnnotationHandlerResolver getAnnotationHandlerResolver() {
        return resolver;
    }
}
