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
package org.instancio.test.features.assign;

import org.instancio.Assignment;
import org.instancio.Instancio;
import org.instancio.When;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatObject;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.field;


@FeatureTag(Feature.ASSIGN)
@ExtendWith(InstancioExtension.class)
class AssignValueOfTest {

    private static final String EXPECTED = "foo";

    @MethodSource
    private static Stream<Arguments> args() {
        final Supplier<String> supplier = () -> EXPECTED;
        final Generator<String> generator = random -> EXPECTED;
        return Stream.of(
                Arguments.of(valueOf(StringsGhi::getH).set(EXPECTED)),
                Arguments.of(valueOf(StringsGhi::getH).generate(gen -> gen.oneOf(EXPECTED))),
                Arguments.of(valueOf(StringsGhi::getH).generate(Instancio.gen().oneOf(EXPECTED))),
                Arguments.of(valueOf(StringsGhi::getH).supply(supplier)),
                Arguments.of(valueOf(StringsGhi::getH).supply(generator)));
    }

    @MethodSource("args")
    @ParameterizedTest
    void verify(final Assignment assignment) {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(assignment)
                .create();

        assertThat(result.def.ghi.h).isEqualTo(EXPECTED);
    }

    @Test
    void valueOfTo() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(valueOf(StringsGhi::getH).to(StringsDef::getE))
                .create();

        assertThat(result.def.e).isEqualTo(result.def.ghi.h);
    }

    @Test
    void valueOfAs() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(valueOf(StringsGhi::getH)
                        .to(StringsDef::getE)
                        .as((String s) -> "prefix-" + s))
                .create();

        assertThat(result.def.e).isEqualTo("prefix-" + result.def.ghi.h);
    }

    @Test
    void valueOfWhen() {
        final int size = 100;
        final List<StringsAbc> results = Instancio.ofList(StringsAbc.class)
                .size(size)
                .generate(field(StringsGhi::getH), gen -> gen.oneOf("H1", "H2"))
                .assign(valueOf(StringsGhi::getH)
                        .to(StringsDef::getE)
                        .when(When.is("H1")))
                .create();

        assertThat(results).hasSize(size).allSatisfy(result -> {
            if ("H1".equals(result.def.ghi.h)) {
                assertThat(result.def.e).isEqualTo("H1");
            } else {
                assertThat(result.def.e).isNotEqualTo("H1");
            }
        });
    }

    @Test
    void valueOfAsWhen() {
        final int size = 100;
        final List<StringsAbc> results = Instancio.ofList(StringsAbc.class)
                .size(size)
                .generate(field(StringsGhi::getH), gen -> gen.oneOf("H1", "H2"))
                .assign(valueOf(StringsGhi::getH)
                        .to(StringsDef::getE)
                        .as((String s) -> "E1-" + s)
                        .when(When.is("H1")))
                .create();

        assertThat(results).hasSize(size).allSatisfy(result -> {
            if ("H1".equals(result.def.ghi.h)) {
                assertThat(result.def.e).isEqualTo("E1-H1");
            } else {
                assertThat(result.def.e).doesNotContain("H1");
            }
        });
    }

    @Test
    void valueOfSetPojo() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .assign(valueOf(StringsDef.class).set(new StringsDef()))
                .create();

        assertThatObject(result.def)
                .as("object via set() should not be populated")
                .isNotNull()
                .hasAllNullFieldsOrProperties();
    }

    @Test
    void valueOfSupplyCollectionElement() {
        final Address result = Instancio.of(Address.class)
                .assign(valueOf(Phone.class).supply(() -> new Phone()))
                .create();

        assertThat(result.getPhoneNumbers()).isNotEmpty().allSatisfy(phone ->
                assertThatObject(phone)
                        .as("object via supply() should not be populated")
                        .hasAllNullFieldsOrProperties());
    }

    @Test
    void valueOfGenerateCollectionElementField() {
        final Address result = Instancio.of(Address.class)
                .assign(valueOf(Phone::getNumber).generate(gen -> gen.string().digits()))
                .create();

        assertThat(result.getPhoneNumbers()).isNotEmpty().allSatisfy(phone ->
                assertThat(phone.getNumber()).containsOnlyDigits());
    }

    @Test
    void valueOfGenerateCollectionSubtype() {
        ListString result = Instancio.of(ListString.class)
                .assign(valueOf(ListString::getList).generate(gen -> gen.collection().subtype(LinkedList.class)))
                .create();

        assertThat(result.getList())
                .isExactlyInstanceOf(LinkedList.class)
                .doesNotContainNull();
    }

}
