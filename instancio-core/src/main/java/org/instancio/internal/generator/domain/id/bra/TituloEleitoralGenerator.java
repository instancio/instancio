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
package org.instancio.internal.generator.domain.id.bra;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.bra.TituloEleitoralSpec;
import org.instancio.internal.generator.AbstractGenerator;

public class TituloEleitoralGenerator extends AbstractGenerator<String> implements TituloEleitoralSpec {

    private static final int TITULO_LENGTH = 8;

    public TituloEleitoralGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public String apiMethod() {
        return "tituloEleitoral()";
    }

    @Override
    public TituloEleitoralGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(Random random) {
        String baseTitulo = random.digits(TITULO_LENGTH);
        int firstDigitVerifier = generateFirstDigit(baseTitulo);
        int stateCode = random.intRange(1, 28);
        int secondDigitVerifier = generateSecondDigit(firstDigitVerifier, stateCode);
        return baseTitulo + (stateCode < 10 ? "0" + stateCode : stateCode) + firstDigitVerifier + secondDigitVerifier;
    }


    /**
     * Generates the first Titulo eleitoral digit verifier
     *
     * @param baseTitulo The base random Titulo
     * @return the first Titulo eleitoral digit verifier
     */
    private int generateFirstDigit(String baseTitulo) {
        return generateDigit(baseTitulo, 2);
    }

    /**
     * Generates the second Titulo Eleitoral digit verifier
     *
     * @param firstDigit The first Titulo Eleitoral digit verifier
     * @param stateCode  The state code of the Titulo Eleitoral
     * @return the second Titulo Eleitoral verifier
     */
    private int generateSecondDigit(int firstDigit, int stateCode) {
        if (stateCode < 10) {
            return generateDigit(String.format("0%d%d", stateCode, firstDigit), 7);
        }
        return generateDigit(String.format("%d%d", stateCode, firstDigit), 7);
    }

    private int generateDigit(String baseNumber, int initialWeight) {
        int weight = initialWeight;
        int sum = 0;
        for (int i = 0; i < baseNumber.length(); i++) {
            char digit = baseNumber.charAt(i);
            sum += Character.getNumericValue(digit) * weight;
            weight++;
        }

        if (sum % 11 == 10) {
            return 0;
        }
        return sum % 11;
    }
}
