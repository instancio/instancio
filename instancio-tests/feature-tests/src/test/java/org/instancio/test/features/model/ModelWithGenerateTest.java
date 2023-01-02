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
package org.instancio.test.features.model;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;

@FeatureTag({Feature.MODEL, Feature.GENERATE})
class ModelWithGenerateTest {

    @Test
    void generate() {
        final long longValue = -111;
        final int intValue = -222;
        final short shortValue = -333;

        final Model<SupportedNumericTypes> model = Instancio.of(SupportedNumericTypes.class)
                .generate(field("primitiveLong"), gen -> gen.longs().range(longValue, longValue))
                .generate(allInts(), gen -> gen.ints().range(intValue, intValue))
                .generate(fields().ofType(short.class), gen -> gen.shorts().range(shortValue, shortValue))
                .toModel();

        final SupportedNumericTypes result = Instancio.create(model);
        assertThat(result.getPrimitiveInt()).isEqualTo(intValue);
        assertThat(result.getIntegerWrapper()).isEqualTo(intValue);
        assertThat(result.getPrimitiveLong()).isEqualTo(longValue);
        assertThat(result.getLongWrapper()).isNotNull().isNotEqualTo(longValue);
        assertThat(result.getPrimitiveShort()).isEqualTo(shortValue);
        assertThat(result.getShortWrapper()).isNotNull().isNotEqualTo(shortValue);
    }

    @Test
    void generateCollectionUsingSelectorGroup() {
        final Model<TwoListsOfItemString> sourceModel = Instancio.of(TwoListsOfItemString.class)
                .generate(all(
                        field(TwoListsOfItemString.class, "list1"),
                        field(TwoListsOfItemString.class, "list2")), gen -> gen.collection().size(1))
                .toModel();

        final Model<TwoListsOfItemString> derivedModel = Instancio.of(sourceModel)
                .generate(all(
                        field(TwoListsOfItemString.class, "list1").within(scope(TwoListsOfItemString.class)),
                        field(TwoListsOfItemString.class, "list2")), gen -> gen.collection().size(2))
                .toModel();

        final TwoListsOfItemString result1 = Instancio.create(sourceModel);
        assertThat(result1.getList1()).hasSize(1);
        assertThat(result1.getList2()).hasSize(1);

        final TwoListsOfItemString result2 = Instancio.of(derivedModel)
                .lenient()
                .create();

        assertThat(result2.getList1()).hasSize(2);
        assertThat(result2.getList2()).hasSize(2);
    }
}