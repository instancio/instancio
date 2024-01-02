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
package org.instancio.test.pojo.beanvalidation;

import lombok.Data;
import org.hibernate.validator.constraints.CreditCardNumber;
import org.hibernate.validator.constraints.EAN;
import org.hibernate.validator.constraints.ISBN;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.LuhnCheck;
import org.hibernate.validator.constraints.Mod10Check;
import org.hibernate.validator.constraints.Mod11Check;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;
import org.hibernate.validator.constraints.UUID;
import org.hibernate.validator.constraints.UniqueElements;
import org.hibernate.validator.constraints.pl.NIP;
import org.hibernate.validator.constraints.pl.PESEL;
import org.hibernate.validator.constraints.pl.REGON;
import org.hibernate.validator.constraints.time.DurationMax;
import org.hibernate.validator.constraints.time.DurationMin;
import org.instancio.test.support.pojo.person.Gender;

/**
 * POJO for verifying that invalid annotation declaration are ignored.
 */
@Data
public class InvalidAnnotationBV {

    @CreditCardNumber
    private Gender g1;
    @DurationMin
    private Gender g2;
    @DurationMax
    private Gender g3;
    @EAN
    private Gender g4;
    @ISBN
    private Gender g5;
    @Length
    private Gender g6;
    @LuhnCheck
    private Gender g7;
    @Mod10Check
    private Gender g8;
    @Mod11Check
    private Gender g9;
    @Range
    private Gender g10;
    @UniqueElements
    private Gender g11;
    @URL
    private Gender g12;
    @UUID
    private Gender g13;
    @NIP
    private Gender g14;
    @PESEL
    private Gender g15;
    @REGON
    private Gender g16;

}
