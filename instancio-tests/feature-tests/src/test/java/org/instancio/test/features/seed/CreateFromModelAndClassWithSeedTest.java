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
package org.instancio.test.features.seed;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.test.support.pojo.basic.IntegerHolder;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allBytes;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allShorts;
import static org.instancio.Select.field;

/**
 * Verify that create(Model) and create(Class) generate same values given the same seed.
 */
@FeatureTag(Feature.WITH_SEED)
class CreateFromModelAndClassWithSeedTest {

    private static final int SEED = 123;

    @Test
    void integerHolder() {
        final Model<IntegerHolder> model = Instancio.of(IntegerHolder.class)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE))
                .withSeed(SEED)
                .toModel();

        final IntegerHolder obj1 = Instancio.create(model);

        final IntegerHolder obj2 = Instancio.of(IntegerHolder.class)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE))
                .withSeed(SEED)
                .create();

        assertThat(obj1).isEqualTo(obj2);
    }

    @Test
    void uuid() {
        final Model<UUID> model = Instancio.of(UUID.class).withSeed(SEED).toModel();
        final UUID obj1 = Instancio.of(UUID.class).withSeed(SEED).create();
        final UUID obj2 = Instancio.of(UUID.class).withSeed(SEED).create();
        final UUID obj3 = Instancio.create(model);
        final UUID obj4 = Instancio.create(model);

        assertThat(obj1)
                .isEqualTo(obj2)
                .isEqualTo(obj3)
                .isEqualTo(obj4);
    }

    @Test
    void person() {
        final Model<Person> personModel = Instancio.of(Person.class)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE))
                .withSeed(SEED).toModel();

        final Person obj1 = Instancio.of(Person.class)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE))
                .withSeed(SEED).create();

        final Person obj2 = Instancio.of(Person.class)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE))
                .withSeed(SEED).create();

        final Person obj3 = Instancio.create(personModel);
        final Person obj4 = Instancio.create(personModel);

        assertThat(obj1)
                .isEqualTo(obj2)
                .isEqualTo(obj3)
                .isEqualTo(obj4);
    }

    @Test
    void supportedNumericTypes() {
        for (int i = 0; i < 10; i++) {
            final SupportedNumericTypes result1 = generateUsingCreateClass();
            final SupportedNumericTypes result2 = generateUsingCreateClass();
            final SupportedNumericTypes result3 = generateUsingModel();
            final SupportedNumericTypes result4 = generateUsingModel();

            assertThat(result1)
                    .isEqualTo(result2)
                    .isEqualTo(result3)
                    .isEqualTo(result4);
        }
    }

    private SupportedNumericTypes generateUsingCreateClass() {
        return buildParameters().create();
    }

    private SupportedNumericTypes generateUsingModel() {
        final Model<SupportedNumericTypes> model = buildParameters().toModel();
        return Instancio.create(model);
    }

    private InstancioApi<SupportedNumericTypes> buildParameters() {
        return Instancio.of(SupportedNumericTypes.class)
                .withNullable(allBytes())
                .ignore(allShorts())
                .supply(field("primitiveDouble"), () -> 124.35d)
                .generate(allInts(), gen -> gen.ints().min(Integer.MIN_VALUE).max(Integer.MAX_VALUE))
                .generate(field("primitiveFloat"), gen -> gen.floats().min(Float.MIN_VALUE))
                .withSeed(SEED);
    }
}