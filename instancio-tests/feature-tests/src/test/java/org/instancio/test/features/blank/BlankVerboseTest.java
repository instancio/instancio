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
package org.instancio.test.features.blank;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.RunWith;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

@FeatureTag(Feature.CARTESIAN_PRODUCT)
@ExtendWith(InstancioExtension.class)
class BlankVerboseTest {

    private final PrintStream standardOut = System.out;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    void tearDown() {
        System.setOut(standardOut);
    }

    /**
     * Disable Method Assignment since that affects the {@code verbose()}
     * output (by including setter methods).
     */
    @Test
    @RunWith.FieldAssignmentOnly
    void verbose() {
        Instancio.ofBlank(Person.class)
                .withBlank(all(Address.class))
                .withBlank(field(Address::getCity))
                .withBlank(fields().named("country"))
                .verbose()
                .create();

        final String actual = outputStreamCaptor.toString()
                .replaceAll("\r", "")
                .replaceAll("\n", "");

        // The selector specified by the user should be reported in verbose output,
        // but blank selectors should not be included.
        assertThat(actual).contains("### Selectors" +
                "Selectors and matching nodes, if any:" +
                "________________________________________________________________________________________"
        );
    }
}
