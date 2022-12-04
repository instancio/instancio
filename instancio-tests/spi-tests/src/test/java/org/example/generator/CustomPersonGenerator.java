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
package org.example.generator;

import org.assertj.core.api.Fail;
import org.instancio.Random;
import org.instancio.generator.Generator;
import org.instancio.generator.GeneratorContext;
import org.instancio.test.support.pojo.person.Person;

/**
 * Used to verify that generators loaded via SPI are initialised exactly once.
 */
public class CustomPersonGenerator implements Generator<Person> {
    public static final String PERSON_NAME = "foo";

    private boolean isInitialised;

    @Override
    public void init(final GeneratorContext context) {

        // Ensure generators loaded from SPI are initialised exactly once
        if (isInitialised) {
            fail();
        }
        isInitialised = true;
    }

    @Override
    public Person generate(final Random random) {
        if (!isInitialised) {
            fail();
        }
        return Person.builder().name(PERSON_NAME).build();
    }

    private void fail() {
        Fail.fail("Expected %s.init() to be called only once!", getClass().getSimpleName());
    }
}
