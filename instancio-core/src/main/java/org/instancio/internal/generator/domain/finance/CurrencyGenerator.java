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
package org.instancio.internal.generator.domain.finance;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.CurrencySpec;
import org.instancio.internal.generator.AbstractGenerator;

public class CurrencyGenerator extends AbstractGenerator<String> implements CurrencySpec {

    private enum ValueType {
        CURRENCY_CODE, SYMBOL
    }

    private static final InternalCurrency[] CURRENCIES = InternalCurrency.values();

    private ValueType valueType = ValueType.CURRENCY_CODE;

    public CurrencyGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "currency()";
    }

    @Override
    public CurrencyGenerator code() {
        return valueType(ValueType.CURRENCY_CODE);
    }

    @Override
    public CurrencyGenerator symbol() {
        return valueType(ValueType.SYMBOL);
    }

    private CurrencyGenerator valueType(final ValueType valueType) {
        this.valueType = valueType;
        return this;
    }

    @Override
    public CurrencyGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(final Random random) {
        final InternalCurrency currency = random.oneOf(CURRENCIES);
        if (valueType == ValueType.SYMBOL) {
            return currency.symbol;
        }
        return currency.code;
    }

    /**
     * Internal currency enum used instead of {@link java.util.Currency}
     * to provide custom display symbols that are not supported by
     * the standard JDK currency API.
     */
    @SuppressWarnings("PMD.ExcessivePublicCount")
    private enum InternalCurrency {
        AED("AED", "د.إ"),
        ALL("ALL", "L"),
        ARS("ARS", "AR$"),
        AUD("AUD", "A$"),
        AZN("AZN", "₼"),
        BBD("BBD", "B$"),
        BDT("BDT", "৳"),
        BGN("BGN", "лв"),
        BHD("BHD", "د.ب."),
        BRL("BRL", "R$"),
        BSD("BSD", "B$"),
        BWP("BWP", "P"),
        CAD("CAD", "CA$"),
        CHF("CHF", "Fr"),
        CLP("CLP", "CL$"),
        CNY("CNY", "¥"),
        COP("COP", "CO$"),
        CRC("CRC", "₡"),
        CZK("CZK", "Kč"),
        DKK("DKK", "kr"),
        DOP("DOP", "RD$"),
        DZD("DZD", "د.ج"),
        EGP("EGP", "£"),
        EUR("EUR", "€"),
        FJD("FJD", "FJ$"),
        GBP("GBP", "£"),
        GEL("GEL", "₾"),
        GHS("GHS", "GH₵"),
        GTQ("GTQ", "Q"),
        HKD("HKD", "HK$"),
        HNL("HNL", "L"),
        HUF("HUF", "Ft"),
        IDR("IDR", "Rp"),
        INR("INR", "₹"),
        IQD("IQD", "ع.د"),
        ISK("ISK", "kr"),
        JMD("JMD", "J$"),
        JOD("JOD", "JD"),
        JPY("JPY", "¥"),
        KES("KES", "KSh"),
        KHR("KHR", "៛"),
        KRW("KRW", "₩"),
        KWD("KWD", "KD"),
        KZT("KZT", "₸"),
        LAK("LAK", "₭"),
        LBP("LBP", "ل.ل"),
        LKR("LKR", "₨"),
        MAD("MAD", "د.م."),
        MMK("MMK", "K"),
        MNT("MNT", "₮"),
        MUR("MUR", "₨"),
        MVR("MVR", "Rf"),
        MXN("MXN", "MX$"),
        MYR("MYR", "RM"),
        NGN("NGN", "₦"),
        NOK("NOK", "kr"),
        NZD("NZD", "NZ$"),
        OMR("OMR", "﷼"),
        PEN("PEN", "S/."),
        PGK("PGK", "K"),
        PHP("PHP", "₱"),
        PKR("PKR", "₨"),
        PLN("PLN", "zł"),
        PYG("PYG", "₲"),
        QAR("QAR", "﷼"),
        RON("RON", "lei"),
        RSD("RSD", "дин."),
        RUB("RUB", "₽"),
        SAR("SAR", "﷼"),
        SCR("SCR", "₨"),
        SEK("SEK", "kr"),
        SGD("SGD", "S$"),
        THB("THB", "฿"),
        TND("TND", "د.ت"),
        TRY("TRY", "₺"),
        TTD("TTD", "TT$"),
        TWD("TWD", "NT$"),
        TZS("TZS", "TSh"),
        UAH("UAH", "₴"),
        UGX("UGX", "USh"),
        USD("USD", "US$"),
        UYU("UYU", "$U"),
        VND("VND", "₫"),
        XCD("XCD", "EC$"),
        XPF("XPF", "₣"),
        ZAR("ZAR", "R"),
        ZMW("ZMW", "ZK");

        private final String code;
        private final String symbol;

        InternalCurrency(final String code, final String symbol) {
            this.code = code;
            this.symbol = symbol;
        }
    }
}