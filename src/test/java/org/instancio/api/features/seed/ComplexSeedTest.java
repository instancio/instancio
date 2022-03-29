/*
 *  Copyright 2022 the original author or authors.
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
package org.instancio.api.features.seed;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.pojo.basic.SupportedNumericTypes;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Bindings.allBytes;
import static org.instancio.Bindings.allInts;
import static org.instancio.Bindings.allShorts;
import static org.instancio.Bindings.field;

class ComplexSeedTest {

    private static final int SEED = 123;

    @Test
    void verifyComplex() {
        final SupportedNumericTypes result1 = generateUsingCreateClass();
        final SupportedNumericTypes result2 = generateUsingCreateClass();
        final SupportedNumericTypes result3 = generateUsingModel();


        assertThat(result1).isEqualTo(result3);
//        assertThat(result1).isEqualTo(result2);
//        assertThat(result2).isEqualTo(result3);

        //        .isEqualTo(result3);
    }

    private SupportedNumericTypes generateUsingCreateClass() {
        return Instancio.of(SupportedNumericTypes.class)
                .withNullable(allBytes())
                .ignore(allShorts())
                .supply(field("primitiveDouble"), () -> 124.35d)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE).max(Integer.MAX_VALUE))
                .generate(field("primitiveFloat"), gen -> gen.floats().min(Float.MIN_VALUE))
                .withSeed(SEED)
                .create();
    }

    private SupportedNumericTypes generateUsingModel() {
        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .withNullable(allBytes())
                .ignore(allShorts())
                .supply(field("primitiveDouble"), () -> 124.35d)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE).max(Integer.MAX_VALUE))
                .generate(field("primitiveFloat"), gen -> gen.floats().min(Float.MIN_VALUE))
                .withSeed(SEED)
                .toModel();

        return Instancio.create(model);
    }
}
