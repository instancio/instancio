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
package org.instancio.test.java16.generator;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.PersonRecord;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@ExtendWith(InstancioExtension.class)
class CustomRecordGeneratorTest {

    @Test
    @DisplayName("POPULATE_NULLS does not apply records since they are read-only")
    void customRecordGenerator() {
        final String name = "John";
        final int age = 25;

        final PersonRecord result = Instancio.of(PersonRecord.class)
                .supply(all(PersonRecord.class), new Generator<PersonRecord>() {
                    @Override
                    public PersonRecord generate(final Random random) {
                        return new PersonRecord(name, age, null);
                    }

                    @Override
                    public Hints hints() {
                        return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS);
                    }
                })
                .create();

        assertThat(result.name()).isEqualTo(name);
        assertThat(result.age()).isEqualTo(age);
        assertThat(result.address())
                .as("NULLS is ignored since records cannot be modified")
                .isNull();
    }
}
