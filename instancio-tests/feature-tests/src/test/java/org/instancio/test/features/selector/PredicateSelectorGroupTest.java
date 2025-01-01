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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.SelectorGroup;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.SELECT_GROUP})
@ExtendWith(InstancioExtension.class)
class PredicateSelectorGroupTest {

    @Test
    void groupOfFields() {
        final String expected = "foo";

        final StringsAbc result = Instancio.of(StringsAbc.class)
                // Verify:
                //  - predicate field selector
                //  - predicate field builders
                //  + both of the above with atDepth()
                .set(all(
                        fields(f -> f.getName().equals("a")),
                        fields(f -> f.getName().equals("d")).atDepth(2),
                        fields(f -> f.getName().equals("g")).atDepth(d -> d == 3),
                        fields().named("b"),
                        fields().named("e").atDepth(2),
                        fields().named("h").atDepth(d -> d == 3)
                ), expected)
                .create();

        assertThat(result.a)
                .isEqualTo(result.b)
                .isEqualTo(result.def.d)
                .isEqualTo(result.def.e)
                .isEqualTo(result.def.ghi.g)
                .isEqualTo(result.def.ghi.h)
                .isEqualTo(expected);

        assertThat(result.c).isNotEqualTo(expected);
        assertThat(result.def.f).isNotEqualTo(expected);
        assertThat(result.def.ghi.i).isNotEqualTo(expected);
    }

    @Test
    void groupOfTypes() {
        final SupportedNumericTypes result = Instancio.of(SupportedNumericTypes.class)
                .set(all(
                        types().of(Integer.class),
                        types(t -> t == Long.class)), null)
                .create();

        assertThat(result.getIntegerWrapper()).isNull();
        assertThat(result.getLongWrapper()).isNull();
    }

    private static Stream<Arguments> groupOfTypesAtDepth() {
        final SelectorGroup group1 = all(
                types().of(String.class).atDepth(1),
                types(t -> t == String.class).atDepth(3));

        final SelectorGroup group2 = all(
                types().of(String.class).atDepth(d -> d == 1),
                types(t -> t == String.class).atDepth(d -> d == 3));

        return Stream.of(Arguments.of(group1), Arguments.of(group2));
    }

    @MethodSource("groupOfTypesAtDepth")
    @ParameterizedTest
    void groupOfTypesAtDepth(final SelectorGroup selectorGroup) {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .set(selectorGroup, "foo")
                .create();

        assertThat(result.a)
                .isEqualTo(result.b)
                .isEqualTo(result.c)
                .isEqualTo(result.def.ghi.g)
                .isEqualTo(result.def.ghi.h)
                .isEqualTo(result.def.ghi.i)
                .isEqualTo("foo");

        assertThat(result.def.d).isNotEqualTo("foo");
        assertThat(result.def.e).isNotEqualTo("foo");
        assertThat(result.def.f).isNotEqualTo("foo");
    }
}
