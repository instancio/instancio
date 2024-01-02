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
import org.instancio.test.support.pojo.person.Gender;

import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Email;
import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Negative;
import javax.validation.constraints.NegativeOrZero;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * POJO for verifying that invalid annotation declaration are ignored.
 */
@Data
public class InvalidAnnotationBV {

    @AssertFalse
    private Gender g1;
    @AssertTrue
    private Gender g2;
    @DecimalMax("1")
    private Gender g3;
    @DecimalMin("1")
    private Gender g4;
    @Digits(integer = 3, fraction = 2)
    private Gender g5;
    @Email
    private Gender g6;
    @Future
    private Gender g7;
    @FutureOrPresent
    private Gender g8;
    @Max(1)
    private Gender g9;
    @Min(1)
    private Gender g10;
    @Negative
    private Gender g11;
    @NegativeOrZero
    private Gender g;
    @NotBlank
    private Gender g12;
    @NotEmpty
    private Gender g13;
    @NotNull
    private Gender g14;
    @Past
    private Gender g15;
    @PastOrPresent
    private Gender g16;
    @Positive
    private Gender g17;
    @PositiveOrZero
    private Gender g18;
    @Size(min = 1)
    private Gender g19;
}
