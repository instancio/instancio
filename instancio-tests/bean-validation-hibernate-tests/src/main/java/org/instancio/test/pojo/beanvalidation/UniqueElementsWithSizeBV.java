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
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import java.util.List;

@Data
public class UniqueElementsWithSizeBV {

    public static final int MIN_SIZE = 20;
    public static final int MAX_SIZE = 26;

    @NotNull
    @Size(min = MIN_SIZE, max = MAX_SIZE)
    @UniqueElements
    private List<Character> list;

    // Unsupported type for @UniqueElements, the annotation should be ignored.
    @NotNull
    @UniqueElements
    private String string;
}
