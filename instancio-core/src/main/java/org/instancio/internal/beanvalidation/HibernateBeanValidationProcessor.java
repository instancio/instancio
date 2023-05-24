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
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.constraints.Mod11Check;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.checksum.LuhnGenerator;
import org.instancio.internal.generator.checksum.Mod10Generator;
import org.instancio.internal.generator.checksum.Mod11Generator;
import org.instancio.internal.generator.domain.finance.CreditCardNumberGenerator;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.domain.id.IsbnGenerator;
import org.instancio.internal.generator.domain.internet.EmailGenerator;
import org.instancio.internal.generator.net.URLGenerator;
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
        // Instancio aims to support Hibernate validator 5.x, in which Email is not deprecated
        //noinspection deprecation
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(Email.class, ((annotation, context) -> new EmailGenerator(context))) // NOSONAR
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(LuhnCheck.class, ((annotation, context) -> getLuhnGenerator((LuhnCheck) annotation, context)))
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(Mod10Check.class, ((annotation, context) -> getMod10Generator((Mod10Check) annotation, context)))
        );
        runIgnoringTheNoClassDefFoundError(() ->
                map.put(Mod11Check.class, ((annotation, context) -> getMod11Generator((Mod11Check) annotation, context)))
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

    @NotNull
    private static EanGenerator getEanGenerator(final EAN ean, final GeneratorContext context) {
        final EanGenerator generator = new EanGenerator(context);
        if (ean.type() == EAN.Type.EAN8) {
            generator.type8();
        }
        return generator;
    }

    private static LuhnGenerator getLuhnGenerator(final LuhnCheck luhn, final GeneratorContext context) {
        final LuhnGenerator generator = new LuhnGenerator(context)
                .startIndex(luhn.startIndex())
                .endIndex(luhn.endIndex());

        if (luhn.checkDigitIndex() != -1) {
            generator.checkDigitIndex(luhn.checkDigitIndex());
        }
        return generator;
    }

    private static Mod10Generator getMod10Generator(final Mod10Check mod10, final GeneratorContext context) {
        final Mod10Generator generator = new Mod10Generator(context)
                .startIndex(mod10.startIndex())
                .endIndex(mod10.endIndex())
                .multiplier(mod10.multiplier())
                .weight(mod10.weight());

        if (mod10.checkDigitIndex() != -1) {
            generator.checkDigitIndex(mod10.checkDigitIndex());
        }
        return generator;
    }

    private static Mod11Generator getMod11Generator(final Mod11Check mod11, final GeneratorContext context) {
        final Mod11Generator generator = new Mod11Generator(context)
                .startIndex(mod11.startIndex())
                .endIndex(mod11.endIndex())
                .threshold(mod11.threshold())
                .treatCheck10As(mod11.treatCheck10As())
                .treatCheck11As(mod11.treatCheck11As());

        if (mod11.processingDirection() == Mod11Check.ProcessingDirection.LEFT_TO_RIGHT) {
            generator.leftToRight();
        }
        if (mod11.checkDigitIndex() != -1) {
            generator.checkDigitIndex(mod11.checkDigitIndex());
        }
        return generator;
    }

    @Override
    protected AnnotationHandlerResolver getAnnotationHandlerResolver() {
        return resolver;
    }
}
