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
package org.instancio.junit.given;

import org.instancio.junit.Given;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.Seed;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GivenFieldSeedAnnotationTest {

    private static final long SEED = -1;

    private static final Set<Object> results = new HashSet<>();

    /**
     * Two fields of the same type should have different
     * values even though the same seed value is used.
     */
    private @Given UUID field1;
    private @Given UUID field2;

    @Seed(SEED)
    @RepeatedTest(4)
    void method1() {
        addAndAssertResults();
    }

    @Seed(SEED)
    @RepeatedTest(4)
    void method2() {
        addAndAssertResults();
    }

    private void addAndAssertResults() {
        assertThat(field1).isNotNull();
        assertThat(field2).isNotNull();

        results.add(field1);
        results.add(field2);
        assertThat(results).hasSize(2);
    }
}
