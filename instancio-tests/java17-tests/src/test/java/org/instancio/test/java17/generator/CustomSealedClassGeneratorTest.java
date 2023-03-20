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
package org.instancio.test.java17.generator;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.java17.classes.FooRecord;
import org.instancio.test.java17.classes.SealedFooSubclass;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.fields;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@ExtendWith(InstancioExtension.class)
class CustomSealedClassGeneratorTest {

    private final Generator<SealedFooSubclass> sealedFooSubclassGenerator = new Generator<SealedFooSubclass>() {
        @Override
        public SealedFooSubclass generate(final Random random) {
            final SealedFooSubclass result = new SealedFooSubclass();
            result.setFooString("foo");
            return result;
        }

        @Override
        public Hints hints() {
            return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS);
        }
    };

    @Test
    void customGenerator() {
        final int fooId = 123;
        final SealedFooSubclass result = Instancio.of(SealedFooSubclass.class)
                .supply(all(SealedFooSubclass.class), sealedFooSubclassGenerator)
                .generate(fields().named("fooId").declaredIn(FooRecord.class),
                        gen -> gen.ints().range(fooId, fooId))
                .create();

        assertThat(result.getFooString()).isEqualTo("foo");
        assertThat(result.getFooDate()).isNotNull();
        assertThat(result.getFooRecord()).isNotNull();
        assertThat(result.getFooRecord().fooId()).isEqualTo(fooId);
        assertThat(result.getFooRecord().fooValue()).isNotBlank();
    }
}
