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

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

public class StringLengthBV {

    @Data
    public static class ThreeFieldsOneMin {
        @NotNull
        private String s1;
        @NotNull
        @Length(min = 20)
        private String s2;
        @NotNull
        private String s3;
    }

    @Data
    public static class WithMinSize {
        @NotNull
        @Length(min = 8)
        private String value;
    }

    @Data
    public static class WithMinSizeZero {
        @NotNull
        @Length
        private String value;
    }

    @Data
    public static class WithMaxSize {
        @NotNull
        @Length(max = 1)
        private String value;
    }

    @Data
    public static class WithMaxSizeZero {
        @NotNull
        @Length(max = 0)
        private String value;
    }

    @Data
    public static class WithMinMaxSize {
        @NotNull
        @Length(min = 19, max = 20)
        private String value;
    }

    @Data
    public static class WithMinMaxEqual {
        @NotNull
        @Length(min = 5, max = 5)
        private String value;
    }
}
