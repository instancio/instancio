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
package org.instancio;

import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.ScopeAssert.assertScope;
import static org.instancio.testsupport.asserts.SelectorAssert.assertSelector;

class SelectTest {

    @Test
    void root() {
        assertSelector(Select.root())
                .isRootSelector()
                .hasNullField()
                .hasNullTargetClass()
                .hasNoScope()
                .hasToString("root()");
    }

    @Test
    void allClass() {
        assertSelector(Select.all(String.class))
                .isClassSelector()
                .hasTargetClass(String.class)
                .hasNullField()
                .hasNoScope();
    }

    @Test
    void group() {
        final List<Selector> selectors = assertSelector(
                Select.all(
                        Select.all(String.class),
                        Select.all(Integer.class).within(Select.scope(Foo.class))))
                .isSelectorGroupOfSize(2)
                .getGroupedSelectors();

        assertSelector(selectors.get(0))
                .isClassSelectorWithNoScope()
                .hasTargetClass(String.class);

        final List<Scope> scopes = assertSelector(selectors.get(1))
                .isClassSelector()
                .hasTargetClass(Integer.class)
                .hasNullField()
                .hasScopeSize(1)
                .getScopes();

        assertScope(scopes.get(0))
                .hasTargetClass(Foo.class)
                .hasNullField();
    }

    @Test
    void field() {
        assertSelector(Select.field("value"))
                .isFieldSelector()
                .hasNullTargetClass()
                .hasFieldName("value")
                .hasNoScope();

        assertSelector(Select.field(StringHolder.class, "value"))
                .isFieldSelector()
                .hasTargetClass(StringHolder.class)
                .hasFieldName("value")
                .hasNoScope();
    }

    @Test
    void fieldWithScope() {
        final List<Scope> scopes = assertSelector(Select.all(String.class)
                .within(
                        Select.scope(Foo.class),
                        Select.scope(Bar.class, "barValue")))
                .isClassSelector()
                .hasTargetClass(String.class)
                .hasNullField()
                .hasScopeSize(2)
                .getScopes();

        assertScope(scopes.get(0))
                .hasTargetClass(Foo.class)
                .hasNullField();

        assertScope(scopes.get(1))
                .hasTargetClass(Bar.class)
                .hasFieldName("barValue");
    }

    @Test
    void allStrings() {
        assertSelector(Select.allStrings())
                .isClassSelectorWithNoScope()
                .hasTargetClass(String.class);
    }

    @Test
    void allBytes() {
        assertSelector(Select.allBytes()).isCoreTypeSelectorWithoutScope(byte.class, Byte.class);
    }

    @Test
    void allFloats() {
        assertSelector(Select.allFloats()).isCoreTypeSelectorWithoutScope(float.class, Float.class);
    }

    @Test
    void allShorts() {
        assertSelector(Select.allShorts()).isCoreTypeSelectorWithoutScope(short.class, Short.class);
    }

    @Test
    void allInts() {
        assertSelector(Select.allInts()).isCoreTypeSelectorWithoutScope(int.class, Integer.class);
    }

    @Test
    void allLongs() {
        assertSelector(Select.allLongs()).isCoreTypeSelectorWithoutScope(long.class, Long.class);
    }

    @Test
    void allDoubles() {
        assertSelector(Select.allDoubles()).isCoreTypeSelectorWithoutScope(double.class, Double.class);
    }

    @Test
    void allBooleans() {
        assertSelector(Select.allBooleans()).isCoreTypeSelectorWithoutScope(boolean.class, Boolean.class);
    }

    @Test
    void allChars() {
        assertSelector(Select.allChars()).isCoreTypeSelectorWithoutScope(char.class, Character.class);
    }

    @Test
    void coreTypeSelectorWithScope() {
        final List<TargetSelector> selectors = assertSelector(Select.allChars()
                .within(
                        Select.scope(Foo.class),
                        Select.scope(Bar.class, "barValue")))
                .isCoreTypeSelector(char.class, Character.class)
                .getAs(PrimitiveAndWrapperSelectorImpl.class)
                .flatten();

        assertThat(selectors).isNotEmpty().allSatisfy(selector -> {
            assertSelector(selector).hasScopeSize(2);

            assertScope(((SelectorImpl) selector).getScopes().get(0))
                    .hasTargetClass(Foo.class)
                    .hasNullField();

            assertScope(((SelectorImpl) selector).getScopes().get(1))
                    .hasTargetClass(Bar.class)
                    .hasFieldName("barValue");
        });
    }

    @Test
    void scope() {
        assertScope(Select.scope(Foo.class))
                .hasTargetClass(Foo.class)
                .hasNullField();

        assertScope(Select.scope(Foo.class, "fooValue"))
                .hasTargetClass(Foo.class)
                .hasFieldName("fooValue");
    }

}
