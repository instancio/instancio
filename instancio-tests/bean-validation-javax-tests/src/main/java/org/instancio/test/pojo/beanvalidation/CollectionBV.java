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

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class CollectionBV {

    @Data
    public static class WithMinSize {
        @NotNull
        @Size(min = 8)
        private Collection<String> value;
    }

    @Data
    public static class WithMinSizeZero {
        @NotNull
        @Size
        private List<String> value;
    }

    @Data
    public static class WithMaxSize {
        @NotNull
        @Size(max = 1)
        private Set<String> value;
    }

    @Data
    public static class WithMaxSizeZero {
        @NotNull
        @Size(max = 0)
        private ArrayList<String> value;
    }

    @Data
    public static class WithMinMaxSize {
        @NotNull
        @Size(min = 19, max = 20)
        private Deque<String> value;
    }

    @Data
    public static class WithMinMaxEqual {
        @NotNull
        @Size(min = 5, max = 5)
        private Queue<String> value;
    }

    @Data
    public static class TypeUse {
        @NotNull
        @Size(min = 3, max = 3)
        private Collection<@NotNull @Digits(integer = 7, fraction = 5) CharSequence> value;
    }
}
