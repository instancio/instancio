/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.spi.tests;

import org.example.spi.CustomGeneratorProvider;
import org.example.spi.CustomGeneratorProvider.InitCountingPojo;
import org.instancio.Instancio;
import org.instancio.generator.Generator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Tests for number of {@link Generator#init} invocations.
 *
 * @see CustomGeneratorProvider#INIT_COUNTING_GENERATOR
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class SpiGeneratorInitTest {

    private static final class InitCountingPojoCollections {
        List<InitCountingPojo> holder1;
        List<InitCountingPojo> holder2;
    }

    private static final class InitCountingPojoContainer {
        InitCountingPojo holder1;
        InitCountingPojo holder2;
    }

    /**
     * This is a bit of a hack. Since the generator we're interested in
     * is also loaded when other tests are run, its init count will not be zero.
     * For this reason, initialise the count to the current init count.
     */
    private static int count = CustomGeneratorProvider.INIT_COUNTING_GENERATOR.getInitCount();

    @Test
    @Order(1)
    @DisplayName("Generator.init() once per collection element")
    void initCountWithCollectionElements() {
        final InitCountingPojoCollections result = Instancio.of(InitCountingPojoCollections.class)
                .generate(field("holder1"), gen -> gen.collection().size(1))
                .generate(field("holder2"), gen -> gen.collection().size(2))
                .create();

        count += 3;

        assertThat(result.holder1).hasSize(1);
        assertThat(result.holder2).hasSize(2);
        assertThat(CustomGeneratorProvider.INIT_COUNTING_GENERATOR.getInitCount()).isEqualTo(count);
    }

    @Test
    @Order(2)
    @DisplayName("Generator.init() once per field")
    void initCountWithFields() {
        final InitCountingPojoContainer result = Instancio.create(InitCountingPojoContainer.class);

        count += 2;

        assertThat(result.holder1).isNotNull();
        assertThat(result.holder2).isNotNull();
        assertThat(CustomGeneratorProvider.INIT_COUNTING_GENERATOR.getInitCount()).isEqualTo(count);
    }
}
