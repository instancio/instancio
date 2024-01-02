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
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

public class ArraySizeBV {

    @Data
    public static class WithMinSize {
        @NotNull
        @Size(min = 8)
        private String[] value;
    }

    @Data
    public static class WithMinSizeZero {
        @NotNull
        @Size
        private int[] value;
    }

    @Data
    public static class WithMaxSize {
        @NotNull
        @Size(max = 1)
        private LocalDate[] value;
    }

    @Data
    public static class WithMaxSizeZero {
        @NotNull
        @Size(max = 0)
        private int[] value;
    }

    @Data
    public static class WithMinMaxSize {
        @NotNull
        @Size(min = 19, max = 20)
        private List<String>[] value;
    }

    @Data
    public static class WithMinMaxEqual {
        @NotNull
        @Size(min = 5, max = 5)
        private Long[] value;
    }
}
