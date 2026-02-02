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
package org.instancio.test.pojo.beanvalidation;

import lombok.Data;
import org.hibernate.validator.constraints.Mod10Check;

public class Mod10CheckBV {

    @Data
    public static class WithDefaults {
        @Mod10Check
        private String value;
    }

    @Data
    public static class WithStartEndIndices {
        @Mod10Check(startIndex = 0, endIndex = 7)
        private String value0;
        @Mod10Check(startIndex = 5, endIndex = 10)
        private String value1;
        @Mod10Check(startIndex = 1, endIndex = 21)
        private String value2;
        @Mod10Check(startIndex = 100, endIndex = 105)
        private String value3;
        @Mod10Check(startIndex = 50, endIndex = 60, multiplier = 5)
        private String value4;
        @Mod10Check(startIndex = 10, endIndex = 30, multiplier = 7, weight = 3)
        private String value5;
    }

    @Data
    public static class WithStartEndAndCheckDigitIndices {
        @Mod10Check(startIndex = 0, endIndex = 7, checkDigitIndex = 8)
        private String value0;
        @Mod10Check(startIndex = 5, endIndex = 10, checkDigitIndex = 3)
        private String value1;
        @Mod10Check(startIndex = 1, endIndex = 21, checkDigitIndex = 0)
        private String value2;
        @Mod10Check(startIndex = 100, endIndex = 105, checkDigitIndex = 150)
        private String value3;
        @Mod10Check(startIndex = 50, endIndex = 60, checkDigitIndex = 70, multiplier = 5)
        private String value4;
        @Mod10Check(startIndex = 10, endIndex = 30, checkDigitIndex = 31, multiplier = 7, weight = 3)
        private String value5;
    }

    @Data
    public static class WithEndAndCheckDigitIndicesEqual {
        @Mod10Check(startIndex = 0, endIndex = 7, checkDigitIndex = 7)
        private String value0;
        @Mod10Check(startIndex = 5, endIndex = 10, checkDigitIndex = 10)
        private String value1;
        @Mod10Check(startIndex = 1, endIndex = 21, checkDigitIndex = 21)
        private String value2;
        @Mod10Check(startIndex = 100, endIndex = 105, checkDigitIndex = 105)
        private String value3;
        @Mod10Check(startIndex = 50, endIndex = 60, checkDigitIndex = 60, multiplier = 5)
        private String value4;
        @Mod10Check(startIndex = 10, endIndex = 30, checkDigitIndex = 30, multiplier = 7, weight = 3)
        private String value5;
    }
}
