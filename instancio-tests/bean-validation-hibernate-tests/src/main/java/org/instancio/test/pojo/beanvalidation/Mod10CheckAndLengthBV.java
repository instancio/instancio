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
package org.instancio.test.pojo.beanvalidation;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Mod10Check;

public class Mod10CheckAndLengthBV {

    @Data
    public static class WithDefaults {
        @NotNull
        @Length(min = 10)
        @Mod10Check
        private String value;
    }

    @Data
    public static class WithStartEndIndices {
        @NotNull
        @Length(min = 8, max = 8)
        @Mod10Check(startIndex = 0, endIndex = 7)
        private String value0;

        @NotNull
        @Length(min = 20, max = 22)
        @Mod10Check(startIndex = 5, endIndex = 10)
        private String value1;

        @NotNull
        @Length(min = 10, max = 15)
        @Mod10Check(startIndex = 3, endIndex = 10, multiplier = 5)
        private String value2;

        @NotNull
        @Length(min = 15, max = 20)
        @Mod10Check(startIndex = 5, endIndex = 15, multiplier = 7, weight = 3)
        private String value3;
    }

    @Data
    public static class WithStartEndAndCheckDigitIndices {
        @NotNull
        @Length(min = 17)
        @Mod10Check(startIndex = 0, endIndex = 7, checkDigitIndex = 8)
        private String value0;

        @NotNull
        @Length(max = 20) // min = 0
        @Mod10Check(startIndex = 5, endIndex = 10, checkDigitIndex = 3)
        private String value1;

        @NotNull
        @Length(min = 20)
        @Mod10Check(startIndex = 3, endIndex = 10, checkDigitIndex = 15, multiplier = 5)
        private String value2;

        @NotNull
        @Length(max = 50) // min = 0
        @Mod10Check(startIndex = 10, endIndex = 25, checkDigitIndex = 5, multiplier = 7, weight = 3)
        private String value3;
    }

    @Data
    public static class WithEndAndCheckDigitIndicesEqual {
        @NotNull
        @Length(min = 17)
        @Mod10Check(startIndex = 0, endIndex = 7, checkDigitIndex = 7)
        private String value0;

        @NotNull
        @Length(max = 20) // min = 0
        @Mod10Check(startIndex = 5, endIndex = 10, checkDigitIndex = 10)
        private String value1;

        @NotNull
        @Length(min = 20)
        @Mod10Check(startIndex = 3, endIndex = 10, checkDigitIndex = 10, multiplier = 5)
        private String value2;

        @NotNull
        @Length(max = 50) // min = 0
        @Mod10Check(startIndex = 10, endIndex = 25, checkDigitIndex = 25, multiplier = 7, weight = 3)
        private String value3;
    }
}
