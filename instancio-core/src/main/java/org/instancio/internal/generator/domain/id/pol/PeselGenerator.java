/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.generator.domain.id.pol;

import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.generator.specs.pol.PeselSpec;
import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.NumberUtils;

import java.time.LocalDate;
import java.util.List;

public class PeselGenerator extends WeightsModCheckGenerator implements PeselSpec {

    private static final List<Integer> PESEL_WEIGHTS = CollectionUtils.asUnmodifiableList(1, 3, 7, 9, 1, 3, 7, 9, 1, 3);
    private static final int GENDER_DIGIT_POSITION = 9;

    private final PeselDateGenerator peselDateGenerator;
    private Gender gender;

    public PeselGenerator(final GeneratorContext context) {
        super(context);
        peselDateGenerator = new PeselDateGenerator(context);
    }

    @Override
    public String apiMethod() {
        return "pesel()";
    }

    @Override
    protected String payload(final Random random) {
        final String birthdate = peselDateGenerator.tryGenerateNonNull(random);
        final String serialNumber = random.digits(payloadLength() - birthdate.length());
        final Gender selectedGender = gender == null ? random.oneOf(Gender.values()) : gender;
        final StringBuilder payload = new StringBuilder(birthdate).append(serialNumber);
        payload.setCharAt(GENDER_DIGIT_POSITION, random.oneOf(selectedGender.digits));
        return payload.toString();
    }

    @Override
    protected char getCheckDigit(final String payload) {
        final int modulo = modulo(payload);
        if (modulo == 0) {
            return '0';
        }
        return NumberUtils.toDigitChar(base() - modulo);
    }

    @Override
    public PeselGenerator birthdate(Generator<LocalDate> localDateGenerator) {
        peselDateGenerator.withLocalDate(localDateGenerator);
        return this;
    }

    @Override
    public PeselGenerator male() {
        gender = Gender.MALE;
        return this;
    }

    @Override
    public PeselGenerator female() {
        gender = Gender.FEMALE;
        return this;
    }

    @Override
    public PeselGenerator nullable() {
        super.nullable();
        return this;
    }

    @Override
    protected List<Integer> weights() {
        return PESEL_WEIGHTS;
    }

    @Override
    protected int base() {
        return 10;
    }

    private enum Gender {
        MALE('1', '3', '5', '7', '9'),
        FEMALE('0', '2', '4', '6', '8');

        private final List<Character> digits;

        Gender(final Character... digits) {
            this.digits = CollectionUtils.asUnmodifiableList(digits);
        }
    }
}
