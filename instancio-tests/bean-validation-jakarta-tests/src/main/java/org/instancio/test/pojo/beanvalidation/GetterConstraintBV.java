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

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.ToString;

public class GetterConstraintBV {

    @ToString
    public static class StringWithGetPrefix {
        private String digits;

        @NotNull
        @Digits(integer = 5, fraction = 0)
        public String getDigits() {
            return digits;
        }
    }

    @ToString
    public static class StringWithNoPrefix {
        private String digits;

        @NotNull
        @Digits(integer = 5, fraction = 0)
        public String digits() {
            return digits;
        }
    }

    @ToString
    public static class BooleanWithGetPrefix {
        private boolean primitiveBoolean;
        private Boolean booleanWrapper;

        @NotNull
        @AssertTrue
        public boolean getPrimitiveBoolean() {
            return primitiveBoolean;
        }

        @NotNull
        @AssertTrue
        public Boolean getBooleanWrapper() {
            return booleanWrapper;
        }
    }

    @ToString
    public static class BooleanWithIsPrefix {
        private boolean primitiveBoolean;
        private Boolean booleanWrapper;

        @NotNull
        @AssertTrue
        public boolean isPrimitiveBoolean() {
            return primitiveBoolean;
        }

        @NotNull
        @AssertTrue
        public Boolean isBooleanWrapper() {
            return booleanWrapper;
        }
    }

    @ToString
    public static class BooleanWithNoPrefix {
        private boolean primitiveBoolean;
        private Boolean booleanWrapper;

        @NotNull
        @AssertTrue
        public boolean primitiveBoolean() {
            return primitiveBoolean;
        }

        @NotNull
        @AssertTrue
        public Boolean booleanWrapper() {
            return booleanWrapper;
        }
    }
}
