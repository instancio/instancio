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
package org.instancio.internal.selectors;

import org.instancio.Select;
import org.instancio.TargetSelector;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.Flattener;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.testsupport.asserts.SelectorAssert;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SelectorGroupImplTest {

    @Nested
    class EqualsAndHashcodeTest {
        @Test
        void verifyEqualsAndHashcodeEmptyGroup() {
            final SelectorGroupImpl one = new SelectorGroupImpl();
            final SelectorGroupImpl two = new SelectorGroupImpl();
            assertThat(one)
                    .isEqualTo(one).hasSameHashCodeAs(one)
                    .isEqualTo(two).hasSameHashCodeAs(two);
        }

        @Test
        void verifyEqualsAndHashcodeWithEqualObjects() {
            final SelectorGroupImpl one = new SelectorGroupImpl(Select.field("foo"), Select.field("bar"));
            final SelectorGroupImpl two = new SelectorGroupImpl(Select.field("foo"), Select.field("bar"));
            assertThat(one).isEqualTo(two).hasSameHashCodeAs(two);
            assertThat(two).isEqualTo(one).hasSameHashCodeAs(one);
        }

        @Test
        void verifyEqualsAndHashcodeWithNonEqualObjects() {
            final SelectorGroupImpl one = new SelectorGroupImpl(Select.field("foo"), Select.field("bar"));
            final SelectorGroupImpl two = new SelectorGroupImpl(Select.field("bar"), Select.field("foo"));
            final SelectorGroupImpl three = new SelectorGroupImpl(Select.field("foo"));
            assertThat(one)
                    .isNotEqualTo(two).doesNotHaveSameHashCodeAs(two)
                    .isNotEqualTo(three).doesNotHaveSameHashCodeAs(three);
        }
    }

    @Test
    void verifyToString() {
        assertThat(new SelectorGroupImpl()).hasToString("all()");

        assertThat(Select.all(
                Select.allBytes().within(Select.scope(Bar.class)),
                Select.field(Foo.class, "fooValue"),
                Select.all(String.class)
        )).hasToString(String.format("all(%n" +
                "\tallBytes(), scope(Bar),%n" +
                "\tfield(Foo, \"fooValue\"),%n" +
                "\tall(String)%n" +
                ")"));
    }

    @Test
    void empty() {
        assertThatThrownBy(Select::all)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Selector group must contain at least one selector");
    }

    @Test
    void flatten() {
        final List<TargetSelector> results = ((Flattener<TargetSelector>) Select.all(
                Select.allBytes().within(Select.scope(Bar.class)),
                Select.field(Foo.class, "fooValue"),
                Select.all(String.class)
        )).flatten();

        assertThat(results).hasSize(4);
        SelectorAssert.assertSelector(results.get(0)).hasTargetClass(byte.class).hasScopeSize(1);
        SelectorAssert.assertSelector(results.get(1)).hasTargetClass(Byte.class).hasScopeSize(1);
        SelectorAssert.assertSelector(results.get(2)).hasTargetClass(Foo.class).hasFieldName("fooValue");
        SelectorAssert.assertSelector(results.get(3)).isClassSelectorWithNoScope().hasTargetClass(String.class);
    }
}
