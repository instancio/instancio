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
package org.instancio.test.java16;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.GenericPairRecord;
import org.instancio.test.support.tags.GenericsTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@GenericsTag
@ExtendWith(InstancioExtension.class)
class GenericPairRecordTest {

    @Test
    void createGenericRecord() {
        final GenericPairRecord<String, Integer> result = Instancio.create(new TypeToken<>() {});
        assertThat(result.left()).isNotBlank().isExactlyInstanceOf(String.class);
        assertThat(result.right()).isExactlyInstanceOf(Integer.class);
    }

    @Test
    void genericRecordAsCollectionElement() {
        final List<GenericPairRecord<String, Integer>> results = Instancio.create(new TypeToken<>() {});

        assertThat(results).isNotEmpty().allSatisfy(result -> {
            assertThat(result.left()).isNotBlank().isExactlyInstanceOf(String.class);
            assertThat(result.right()).isExactlyInstanceOf(Integer.class);
        });
    }
}
