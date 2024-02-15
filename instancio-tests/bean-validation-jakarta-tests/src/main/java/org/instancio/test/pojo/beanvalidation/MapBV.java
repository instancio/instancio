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

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class MapBV {

    @Data
    public static class WithMinSize {
        @NotNull
        @Size(min = 8)
        private Map<String, Long> value;
    }

    @Data
    public static class WithMinSizeZero {
        @NotNull
        @Size
        private SortedMap<Integer, String> value;
    }

    @Data
    public static class WithMaxSize {
        @NotNull
        @Size(max = 1)
        private HashMap<UUID, BigDecimal> value;
    }

    @Data
    public static class WithMaxSizeZero {
        @NotNull
        @Size(max = 0)
        private NavigableMap<Integer, String> value;
    }

    @Data
    public static class WithMinMaxSize {
        @NotNull
        @Size(min = 19, max = 20)
        private TreeMap<String, Character> value;
    }

    @Data
    public static class WithMinMaxEqual {
        @NotNull
        @Size(min = 5, max = 5)
        private Map<Long, Double> value;
    }

    @Data
    public static class TypeUse {
        @NotNull
        private Map<
                @NotNull @Digits(integer = 7, fraction = 5) CharSequence,
                @NotNull @Digits(integer = 3, fraction = 2) CharSequence> value;
    }
}
