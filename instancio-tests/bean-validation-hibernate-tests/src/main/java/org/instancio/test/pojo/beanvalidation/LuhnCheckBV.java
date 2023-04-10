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

import lombok.Data;
import org.hibernate.validator.constraints.LuhnCheck;

public class LuhnCheckBV {

    @Data
    public static class WithDefaults {
        @LuhnCheck
        private String value;
    }

    @Data
    public static class WithStartEndIndices {
        @LuhnCheck(startIndex = 0, endIndex = 7)
        private String value0;
        @LuhnCheck(startIndex = 5, endIndex = 10)
        private String value1;
        @LuhnCheck(startIndex = 1, endIndex = 21)
        private String value2;
        @LuhnCheck(startIndex = 100, endIndex = 105)
        private String value3;
    }

    @Data
    public static class WithStartEndAndCheckDigitIndices {
        @LuhnCheck(startIndex = 0, endIndex = 7, checkDigitIndex = 8)
        private String value0;
        @LuhnCheck(startIndex = 5, endIndex = 10, checkDigitIndex = 3)
        private String value1;
        @LuhnCheck(startIndex = 1, endIndex = 21, checkDigitIndex = 0)
        private String value2;
        @LuhnCheck(startIndex = 100, endIndex = 105, checkDigitIndex = 150)
        private String value3;
    }

    @Data
    public static class WithEndAndCheckDigitIndicesEqual {
        @LuhnCheck(startIndex = 0, endIndex = 7, checkDigitIndex = 7)
        private String value0;
        @LuhnCheck(startIndex = 5, endIndex = 10, checkDigitIndex = 10)
        private String value1;
        @LuhnCheck(startIndex = 1, endIndex = 21, checkDigitIndex = 21)
        private String value2;
        @LuhnCheck(startIndex = 100, endIndex = 105, checkDigitIndex = 105)
        private String value3;
    }
}
