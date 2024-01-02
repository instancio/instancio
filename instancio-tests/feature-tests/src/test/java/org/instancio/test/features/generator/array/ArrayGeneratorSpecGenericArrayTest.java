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
package org.instancio.test.features.generator.array;

import org.instancio.Gen;
import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.internal.util.Constants;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.arrayofsuppliers.GenericArrayHolder;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.PhoneWithType;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class ArrayGeneratorSpecGenericArrayTest {

    private static final int SIZE = Gen.ints().range(0, 3).get();

    @Test
    void create() {
        final GenericArrayHolder<Phone> result = Instancio.create(new TypeToken<GenericArrayHolder<Phone>>() {});

        assertThat(result.getArray())
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Phone.class)
                .allSatisfy(phone -> assertThat(phone).hasNoNullFieldsOrProperties());
    }

    @Test
    void arraySpecWithNoSubtypeSpecified() {
        final GenericArrayHolder<Phone> result = Instancio.of(new TypeToken<GenericArrayHolder<Phone>>() {})
                .generate(field(GenericArrayHolder.class, "array"), gen -> gen.array().length(SIZE))
                .create();

        assertThat(result.getArray())
                .hasSize(SIZE)
                .hasOnlyElementsOfType(Phone.class)
                .allSatisfy(phone -> assertThat(phone).hasNoNullFieldsOrProperties());
    }

    @Test
    void arraySpecWithSubtype() {
        final GenericArrayHolder<Phone> result = Instancio.of(new TypeToken<GenericArrayHolder<Phone>>() {})
                .generate(field(GenericArrayHolder.class, "array"), gen -> gen.array().length(SIZE)
                        .subtype(PhoneWithType[].class))
                .create();

        assertThat(result.getArray())
                .hasSize(SIZE)
                .hasOnlyElementsOfType(PhoneWithType.class)
                .allSatisfy(phone -> assertThat(phone).hasNoNullFieldsOrProperties());
    }
}
