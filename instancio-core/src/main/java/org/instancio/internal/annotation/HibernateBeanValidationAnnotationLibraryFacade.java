/*
 * Copyright 2022-2026 the original author or authors.
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

import org.instancio.generator.GeneratorContext;
import org.instancio.internal.generator.checksum.LuhnGenerator;
import org.instancio.internal.generator.checksum.Mod10Generator;
import org.instancio.internal.generator.checksum.Mod11Generator;
import org.instancio.internal.generator.domain.finance.CreditCardNumberGenerator;
import org.instancio.internal.generator.domain.id.EanGenerator;
import org.instancio.internal.generator.domain.id.IsbnGenerator;
import org.instancio.internal.generator.domain.id.bra.CnpjGenerator;
import org.instancio.internal.generator.domain.id.bra.CpfGenerator;
import org.instancio.internal.generator.domain.id.bra.TituloEleitoralGenerator;
import org.instancio.internal.generator.domain.id.pol.NipGenerator;
import org.instancio.internal.generator.domain.id.pol.PeselGenerator;
import org.instancio.internal.generator.domain.id.pol.RegonGenerator;
import org.instancio.internal.generator.domain.id.rus.InnGenerator;
import org.instancio.internal.generator.net.URLGenerator;
import org.instancio.internal.generator.util.UUIDGenerator;
import org.instancio.internal.util.StringUtils;

final class HibernateBeanValidationAnnotationLibraryFacade extends AbstractAnnotationLibraryFacade {

    HibernateBeanValidationAnnotationLibraryFacade() {
        putPrimary(() -> org.hibernate.validator.constraints.EAN.class,
                (annotation, context) -> getEanGenerator(
                        (org.hibernate.validator.constraints.EAN) annotation, context)
        );

        putPrimary(() -> org.hibernate.validator.constraints.LuhnCheck.class,
                (annotation, context) -> getLuhnGenerator(
                        (org.hibernate.validator.constraints.LuhnCheck) annotation, context));

        putPrimary(() -> org.hibernate.validator.constraints.Mod10Check.class,
                (annotation, context) -> getMod10Generator(
                        (org.hibernate.validator.constraints.Mod10Check) annotation, context));

        putPrimary(() -> org.hibernate.validator.constraints.Mod11Check.class,
                (annotation, context) -> getMod11Generator(
                        (org.hibernate.validator.constraints.Mod11Check) annotation, context));

        putPrimary(() -> org.hibernate.validator.constraints.CreditCardNumber.class,
                (annotation, context) -> new CreditCardNumberGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.ISBN.class,
                (annotation, context) -> new IsbnGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.UUID.class,
                (annotation, context) -> new UUIDGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.URL.class,
                (annotation, context) -> getUrlGenerator(
                        (org.hibernate.validator.constraints.URL) annotation, context));

        putPrimary(() -> org.hibernate.validator.constraints.pl.NIP.class,
                (annotation, context) -> new NipGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.pl.PESEL.class,
                (annotation, context) -> new PeselGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.pl.REGON.class,
                (annotation, context) -> new RegonGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.br.CPF.class,
                (annotation, context) -> new CpfGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.br.CNPJ.class,
                (annotation, context) -> new CnpjGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.br.TituloEleitoral.class,
                (annotation, context) -> new TituloEleitoralGenerator(context));

        putPrimary(() -> org.hibernate.validator.constraints.ru.INN.class,
                (annotation, context) -> getInnGenerator(
                        (org.hibernate.validator.constraints.ru.INN) annotation, context));
    }

    @Override
    protected AnnotationHandlerMap getAnnotationHandlerMap() {
        return HibernateBeanValidationHandlerMap.getInstance();
    }

    private static URLGenerator getUrlGenerator(
            final org.hibernate.validator.constraints.URL url,
            final GeneratorContext context) {

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

    private static EanGenerator getEanGenerator(
            final org.hibernate.validator.constraints.EAN ean,
            final GeneratorContext context) {

        final EanGenerator generator = new EanGenerator(context);
        if (ean.type() == org.hibernate.validator.constraints.EAN.Type.EAN8) {
            generator.type8();
        }
        return generator;
    }

    private static LuhnGenerator getLuhnGenerator(
            final org.hibernate.validator.constraints.LuhnCheck luhn,
            final GeneratorContext context) {

        final LuhnGenerator generator = new LuhnGenerator(context)
                .startIndex(luhn.startIndex())
                .endIndex(luhn.endIndex());

        if (luhn.checkDigitIndex() != -1) {
            generator.checkDigitIndex(luhn.checkDigitIndex());
        }
        return generator;
    }

    private static Mod10Generator getMod10Generator(
            final org.hibernate.validator.constraints.Mod10Check mod10,
            final GeneratorContext context) {

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

    private static Mod11Generator getMod11Generator(
            final org.hibernate.validator.constraints.Mod11Check mod11,
            final GeneratorContext context) {

        final Mod11Generator generator = new Mod11Generator(context)
                .startIndex(mod11.startIndex())
                .endIndex(mod11.endIndex())
                .threshold(mod11.threshold())
                .treatCheck10As(mod11.treatCheck10As())
                .treatCheck11As(mod11.treatCheck11As());

        if (mod11.processingDirection() == org.hibernate.validator.constraints.Mod11Check.ProcessingDirection.LEFT_TO_RIGHT) {
            generator.leftToRight();
        }
        if (mod11.checkDigitIndex() != -1) {
            generator.checkDigitIndex(mod11.checkDigitIndex());
        }
        return generator;
    }

    private static InnGenerator getInnGenerator(
            final org.hibernate.validator.constraints.ru.INN inn,
            final GeneratorContext context) {

        final InnGenerator generator = new InnGenerator(context);

        if (inn.type() == org.hibernate.validator.constraints.ru.INN.Type.INDIVIDUAL) {
            generator.individual();
        } else if (inn.type() == org.hibernate.validator.constraints.ru.INN.Type.JURIDICAL) {
            generator.juridical();
        }

        return generator;
    }
}
