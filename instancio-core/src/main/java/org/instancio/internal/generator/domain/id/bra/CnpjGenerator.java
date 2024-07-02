/*
 * Copyright 2022-2024 the original author or authors.
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
package org.instancio.internal.generator.domain.id.bra;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.bra.CnpjSpec;
import org.instancio.internal.generator.AbstractGenerator;

public class CnpjGenerator extends AbstractGenerator<String> implements CnpjSpec {

    private static final int CNPJ_LENGTH = 12;

    private boolean format;

    public CnpjGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public CnpjGenerator formatted() {
        this.format = true;
        return this;
    }

    @Override
    public String apiMethod() {
        return "cnpj()";
    }

    @Override
    public CnpjGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(Random random) {
        String baseCnpj = random.digits(CNPJ_LENGTH);
        int firstDigitVerifier = generateFirstDigit(baseCnpj);
        int secondDigitVerifier = generateSecondDigit(baseCnpj, firstDigitVerifier);
        if (!format) {
            return baseCnpj + firstDigitVerifier + secondDigitVerifier;
        }
        return format(baseCnpj, String.format("%d%d", firstDigitVerifier, secondDigitVerifier));
    }

    /**
     * Formats the given CNPJ using the following pattern: {@code xx.xxx.xxx/xxxx-xx}
     *
     * @param baseCNPJ The base CNPJ
     * @param digits  The verifier digits
     * @return The CPF formatted
     */
    private String format(String baseCNPJ, String digits) {
        return baseCNPJ.substring(0, 2) +
                '.' +
                baseCNPJ.substring(2, 5) +
                '.' +
                baseCNPJ.substring(5, 8) +
                '/' +
                baseCNPJ.substring(8, 12) +
                '-' +
                digits;
    }

    /**
     * Generates the first CNPJ digit verifier
     *
     * @param baseCnpj The base random CNPJ
     * @return the first CNPJ verifier
     */
    private int generateFirstDigit(String baseCnpj) {
        return generateDigit(baseCnpj, 5);
    }

    /**
     * Generates the second CNPJ digit verifier
     *
     * @param baseCnpj    The base random CNPJ
     * @param firstDigit The first CNPJ digit verifier
     * @return the second CNPJ verifier
     */
    private int generateSecondDigit(String baseCnpj, int firstDigit) {
        return generateDigit(baseCnpj + firstDigit, 6);
    }

    private int generateDigit(String baseCnpj, int initialWeight) {
        int weight = initialWeight;
        int sum = 0;
        for (char digit : baseCnpj.toCharArray()) {
            sum += Character.getNumericValue(digit) * weight;
            weight--;
            if (weight == 1) {
                weight = 9;
            }
        }
        int mod = sum % 11;
        if (mod < 2) {
            return 0;
        }
        return 11 - mod;
    }
}
