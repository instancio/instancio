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
import org.instancio.generator.specs.bra.CpfSpec;
import org.instancio.internal.generator.AbstractGenerator;

public class CpfGenerator extends AbstractGenerator<String> implements CpfSpec {

    private static final int CPF_LENGTH = 9;

    private boolean format;

    public CpfGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public CpfGenerator formatted() {
        this.format = true;
        return this;
    }

    @Override
    public String apiMethod() {
        return "cpf()";
    }

    @Override
    public CpfGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(Random random) {
        String baseCpf = random.digits(CPF_LENGTH);
        int firstDigitVerifier = generateFirstDigit(baseCpf);
        int secondDigitVerifier = generateSecondDigit(baseCpf, firstDigitVerifier);
        if (!format) {
            return baseCpf + firstDigitVerifier + secondDigitVerifier;
        }
        return format(baseCpf, String.format("%d%d", firstDigitVerifier, secondDigitVerifier));
    }

    /**
     * Formats the given CPF using the following pattern: @code{xxx.xxx.xxx-xx}
     *
     * @param baseCPF The base CPF
     * @param digits  The verifier digits
     * @return The CPF formatted
     */
    String format(String baseCPF, String digits) {
        return baseCPF.substring(0, 3) +
                '.' +
                baseCPF.substring(3, 6) +
                '.' +
                baseCPF.substring(6, 9) +
                '-' +
                digits;
    }

    /**
     * Generates the first CPF digit verifier
     *
     * @param baseCpf The base random CPF
     * @return the first CPF verifier
     */
    private int generateFirstDigit(String baseCpf) {
        return generateDigit(baseCpf, 1);
    }

    /**
     * Generates the second CPF digit verifier
     *
     * @param baseCpf    The base random CPF
     * @param firstDigit The first CPF digit verifier
     * @return the second CPF verifier
     */
    private int generateSecondDigit(String baseCpf, int firstDigit) {
        return generateDigit(baseCpf + firstDigit, 0);
    }

    private int generateDigit(String baseCpf, int initialWeight) {
        int weight = initialWeight;
        int sum = 0;
        for (char digit : baseCpf.toCharArray()) {
            sum += Character.getNumericValue(digit) * weight;
            weight++;
        }
        int result = sum % 11;
        if (result == 10) {
            return 0;
        }
        return result;
    }
}
