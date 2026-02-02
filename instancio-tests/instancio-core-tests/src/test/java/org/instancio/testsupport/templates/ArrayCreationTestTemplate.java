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
package org.instancio.testsupport.templates;

import org.junit.jupiter.api.AfterAll;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ArrayCreationTestTemplate<T> extends CreationTestTemplate<T> {

    protected final Set<Object> generatedValues = new HashSet<>();

    protected int minNumberOfGeneratedValues() {
        return NumberOfExecutions.DEFAULT_NUM_TEST_EXECUTIONS * 4;
    }

    @AfterAll
    protected void verifyUniqueValues() {
        assertThat(generatedValues)
                .as("Expected distinct randomly generated values")
                .hasSizeGreaterThanOrEqualTo(minNumberOfGeneratedValues());
    }
}
