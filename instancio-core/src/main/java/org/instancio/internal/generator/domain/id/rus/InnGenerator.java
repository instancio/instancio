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
package org.instancio.internal.generator.domain.id.rus;

import org.instancio.Random;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.rus.InnSpec;
import org.instancio.internal.generator.AbstractGenerator;

public class InnGenerator extends AbstractGenerator<String> implements InnSpec {

    private static final int[] INDIVIDUAL_WEIGHTS_11 = {7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] INDIVIDUAL_WEIGHTS_12 = {3, 7, 2, 4, 10, 3, 5, 9, 4, 6, 8};
    private static final int[] JURIDICAL_WEIGHTS = {2, 4, 10, 3, 5, 9, 4, 6, 8};

    private Type type;

    public InnGenerator(final GeneratorContext context) {
        super(context);
    }

    @Override
    public InnGenerator individual() {
        this.type = Type.INDIVIDUAL;
        return this;
    }

    @Override
    public InnGenerator juridical() {
        this.type = Type.JURIDICAL;
        return this;
    }

    @Override
    public String apiMethod() {
        return "inn()";
    }

    @Override
    public InnGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected String tryGenerateNonNull(Random random) {
        if (type == Type.INDIVIDUAL) {
            return generateIndividualInn(random);
        } else if (type == Type.JURIDICAL) {
            return generateJuridicalInn(random);
        } else {
            return random.trueOrFalse() ? generateIndividualInn(random) : generateJuridicalInn(random);
        }
    }

    private String generateIndividualInn(Random random) {
        String baseInn = random.digits(10);
        int firstCheck = getCheckSum(baseInn, INDIVIDUAL_WEIGHTS_11);
        int secondCheck = getCheckSum(baseInn + firstCheck, INDIVIDUAL_WEIGHTS_12);
        return baseInn + firstCheck + secondCheck;
    }

    private String generateJuridicalInn(Random random) {
        String baseInn = random.digits(9);
        int firstCheck = getCheckSum(baseInn, JURIDICAL_WEIGHTS);
        return baseInn + firstCheck;
    }

    private int getCheckSum(String digits, int... weights) {
        int sum = 0;
        for (int i = 0; i < weights.length; i++) {
            int digit = Character.getNumericValue(digits.charAt(i));
            sum += digit * weights[i];
        }
        return (sum % 11) % 10;
    }

    private enum Type {
        INDIVIDUAL, JURIDICAL
    }
}
