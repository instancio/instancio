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
package org.instancio.test.java16.generator;

import org.instancio.GetMethodSelector;
import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.java16.record.GenericPairRecord;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;

/**
 * NOTE: this test fails in IntelliJ, run it using Maven
 */
@FeatureTag({Feature.GENERATOR, Feature.EMIT_GENERATOR})
@ExtendWith(InstancioExtension.class)
class EmitGeneratorRecordTest {

    private record FooBarRecord(String foo, String bar) {}

    @Test
    void fooBar() {
        final TargetSelector fooAndBar = all(
                field(FooBarRecord::foo),
                field(FooBarRecord::bar));

        final FooBarRecord result = Instancio.of(FooBarRecord.class)
                .generate(fooAndBar, gen -> gen.emit().items("baz"))
                .create();

        assertThat(result.foo())
                .isEqualTo(result.bar())
                .isEqualTo("baz");
    }

    @Test
    void genericPairRecord() {
        final int size = 3;
        final String[] items = {"foo", "bar", "baz"};
        final GetMethodSelector<GenericPairRecord<String, Integer>, String> left = GenericPairRecord::left;

        final List<GenericPairRecord<String, Integer>> result = Instancio.of(
                        new TypeToken<List<GenericPairRecord<String, Integer>>>() {})
                .generate(all(List.class), gen -> gen.collection().size(size))
                .generate(left, gen -> gen.emit().items(items))
                .create();

        assertThat(result).hasSize(size)
                .extracting(GenericPairRecord::left)
                .containsExactly(items);
    }
}
