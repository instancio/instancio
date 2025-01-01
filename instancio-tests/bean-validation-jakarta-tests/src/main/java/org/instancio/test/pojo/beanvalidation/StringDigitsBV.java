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
package org.instancio.test.pojo.beanvalidation;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

public class StringDigitsBV {

    @Data
    public static class OnString {
        @NotNull
        @Digits(integer = 0, fraction = 2)
        private String s0;
        @NotNull
        @Digits(integer = 1, fraction = 0)
        private String s1;
        @NotNull
        @Digits(integer = 15, fraction = 0)
        private String s2;
        @NotNull
        @Digits(integer = 1, fraction = 1)
        private String s3;
        @NotNull
        @Digits(integer = 15, fraction = 20)
        private String s4;
    }

    @Data
    public static class OnCharSequence {
        @NotNull
        @Digits(integer = 3, fraction = 5)
        private CharSequence value;
    }
}
