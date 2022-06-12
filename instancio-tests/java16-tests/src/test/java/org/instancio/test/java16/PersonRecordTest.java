/*
 * Copyright 2022 the original author or authors.
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
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.PersonRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@ExtendWith(InstancioExtension.class)
class PersonRecordTest {

    @Test
    void createPersonRecord() {
        final PersonRecord result = Instancio.create(PersonRecord.class);
        assertThatObject(result).isFullyPopulated();
    }
}
