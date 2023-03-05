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
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.domain.finance.CreditCardNumberGenerator;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.domain.id.IsbnGenerator;
import org.instancio.internal.generator.net.URLGenerator;
import org.instancio.internal.generator.text.LuhnGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;
import org.instancio.internal.util.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.instancio.internal.util.ExceptionHandler.runIgnoringTheNoClassDefFoundError;

final class HibernateBeanValidationProcessor extends AbstractBeanValidationProvider {

    private final HibernateBeanValidationHandlerResolver resolver =
            HibernateBeanValidationHandlerResolver.getInstance();

    HibernateBeanValidationProcessor() {
        super(buildMap());
    }

    private static Map<Class<? extends Annotation>, BiFunction<Annotation, GeneratorContext, Generator<?>>> buildMap() {
        Map<Class<? extends Annotation>, BiFunction<Annotation, GeneratorContext, Generator<?>>> map = new HashMap<>();
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(EAN.class, ((annotation, context) -> getEanGenerator((EAN) annotation, context)))
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(LuhnCheck.class, ((annotation, context) -> getLuhnGenerator((LuhnCheck) annotation, context)))
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(CreditCardNumber.class, ((annotation, context) -> new CreditCardNumberGenerator(context)))
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(ISBN.class, ((annotation, context) -> new IsbnGenerator(context)))
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(UUID.class, ((annotation, context) -> new UUIDGenerator(context)))
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(URL.class, ((annotation, context) -> getUrlGenerator((URL) annotation, context)))
        );
        return map;
    }

    private static URLGenerator getUrlGenerator(final URL url, final GeneratorContext context) {
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

    private static LuhnGenerator getLuhnGenerator(final LuhnCheck luhn, final GeneratorContext context) {
        final LuhnGenerator generator = new LuhnGenerator(context)
                .startIndex(luhn.startIndex())
                .endIndex(luhn.endIndex());

        if (luhn.checkDigitIndex() != -1) {
            generator.checkIndex(luhn.checkDigitIndex());
        }
        return generator;
    }

    @NotNull
    private static EanGenerator getEanGenerator(final EAN ean, final GeneratorContext context) {
        final EanGenerator generator = new EanGenerator(context);
        if (ean.type() == EAN.Type.EAN8) {
            generator.type8();
        }
        return generator;
    }

    @Override
    protected AnnotationHandlerResolver getAnnotationHandlerResolver() {
        return resolver;
    }
}
