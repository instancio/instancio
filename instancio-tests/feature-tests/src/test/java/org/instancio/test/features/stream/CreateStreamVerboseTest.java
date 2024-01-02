/*
 *  Copyright 2022-2024 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.features.stream;

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.STREAM)
@ExtendWith(InstancioExtension.class)
class CreateStreamVerboseTest {

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
     * Should output only once per stream, not per each root object created.
     */
    @Test
    void verbose() {
        final long seed = Gen.longs().get();

        final List<Integer> results = Instancio.of(Integer.class)
                .withSeed(seed)
                .verbose()
                .stream()
                .limit(3)
                .collect(Collectors.toList());

        assertThat(results)
                .hasSize(3)
                .doesNotContainNull();

        assertThat(outputStreamCaptor)
                .asString()
                .containsOnlyOnce("<0:Integer>")
                .containsOnlyOnce("Seed ..................: " + seed);
    }
}