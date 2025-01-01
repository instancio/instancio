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
package org.instancio.internal.selectors;

import org.instancio.Scope;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiMethodSelector;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.testsupport.asserts.ScopeAssert.assertScope;
import static org.instancio.testsupport.asserts.SelectorAssert.assertSelector;

class SelectorProcessorTest {

    private static final Class<?> PERSON_CLASS = Person.class;

    private final SelectorProcessor processor = new SelectorProcessor(
            PERSON_CLASS, Collections.emptyList(), new SetterSelectorHolder());

    private TargetSelector processOne(final TargetSelector selector) {
        final List<TargetSelector> results = processor.process(selector, ApiMethodSelector.NONE);
        assertThat(results).hasSize(1);
        return results.get(0);
    }

    @Test
    void root() {
        final TargetSelector input = Select.root();

        final TargetSelector result = processOne(input);

        assertSelector(result)
                .isRootSelector()
                .hasNullTargetClass()
                .hasNoScope()
                .hasToString("root()")
                .isEqualTo(input);
    }

    @Test
    void fieldName() {
        final Selector input = Select.field("name");

        final TargetSelector result = processOne(input);

        assertSelector(result)
                .isFieldSelector()
                .hasTargetClass(PERSON_CLASS)
                .hasFieldName("name")
                .hasNoScope();
    }

    @Test
    void targetClassAndFieldName() {
        final TargetSelector input = Select.field(PERSON_CLASS, "name");

        final TargetSelector result = processOne(input);

        assertSelector(result)
                .isFieldSelector()
                .hasTargetClass(PERSON_CLASS)
                .hasFieldName("name")
                .hasNoScope();
    }

    @Test
    void allClass() {
        final TargetSelector input = Select.all(String.class);

        final TargetSelector result = processOne(input);

        assertSelector(result)
                .isClassSelector()
                .hasTargetClass(String.class)
                .hasNoScope();
    }

    @Test
    void group() {
        final TargetSelector input = Select.all(
                Select.all(String.class),
                Select.all(Integer.class).within(Select.scope(Foo.class)));

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertThat(results).hasSize(2);

        assertSelector(results.get(0))
                .isClassSelectorWithNoScope()
                .hasTargetClass(String.class);

        final List<Scope> scopes = assertSelector(results.get(1))
                .isClassSelector()
                .hasTargetClass(Integer.class)
                .hasScopeSize(1)
                .getScopes();

        assertScope(scopes.get(0))
                .hasTargetClass(Foo.class)
                .isClassSelector();
    }

    @Test
    void fieldWithScope() {
        final TargetSelector input = Select.field(Phone.class, "number")
                .within(Select.scope(Person.class),
                        Select.scope(Address.class, "phoneNumbers"));

        final TargetSelector result = processOne(input);

        final List<Scope> scopes = assertSelector(result)
                .isFieldSelector()
                .hasTargetClass(Phone.class)
                .hasFieldName("number")
                .hasScopeSize(2)
                .getScopes();

        assertScope(scopes.get(0))
                .isClassSelector()
                .hasTargetClass(Person.class);

        assertScope(scopes.get(1))
                .isFieldSelector()
                .hasTargetClass(Address.class)
                .hasField("phoneNumbers");
    }

    @Test
    void methodName() {
        final TargetSelector input = Select.setter("setName");

        final TargetSelector result = processOne(input);

        assertSelector(result)
                .isSetterSelector()
                .hasTargetClass(PERSON_CLASS)
                .hasMethodName("setName")
                .hasNoScope();
    }

    @Test
    void methodNameAndTargetClass() {
        final TargetSelector input = Select.setter(PERSON_CLASS, "setName");

        final TargetSelector result = processOne(input);

        assertSelector(result)
                .isSetterSelector()
                .hasTargetClass(PERSON_CLASS)
                .hasMethodName("setName")
                .hasNoScope();
    }

    @Test
    void methodWithScope() {
        final TargetSelector input = Select.setter(Phone.class, "setNumber")
                .within(Select.field("address").toScope(), // Scope without target class
                        Select.scope(Address.class, "phoneNumbers"));

        final TargetSelector result = processOne(input);

        final List<Scope> scopes = assertSelector(result)
                .isSetterSelector()
                .hasTargetClass(Phone.class)
                .hasMethodName("setNumber")
                .hasScopeSize(2)
                .getScopes();

        assertScope(scopes.get(0))
                .isFieldSelector()
                .hasTargetClass(Person.class)
                .hasField("address");

        assertScope(scopes.get(1))
                .isFieldSelector()
                .hasTargetClass(Address.class)
                .hasField("phoneNumbers");
    }

    @Test
    void allWithScope() {
        final TargetSelector input = Select.all(String.class)
                .within(Select.scope(Foo.class),
                        Select.scope(Bar.class, "barValue"));

        final TargetSelector result = processOne(input);

        final List<Scope> scopes = assertSelector(result)
                .isClassSelector()
                .hasTargetClass(String.class)
                .hasScopeSize(2)
                .getScopes();

        assertScope(scopes.get(0))
                .isClassSelector()
                .hasTargetClass(Foo.class);

        assertScope(scopes.get(1))
                .isFieldSelector()
                .hasTargetClass(Bar.class)
                .hasField("barValue");
    }

    @Test
    void allStrings() {
        final TargetSelector input = Select.allStrings();

        final TargetSelector result = processOne(input);

        assertSelector(result)
                .isClassSelectorWithNoScope()
                .hasTargetClass(String.class);
    }


    @Test
    void allBytes() {
        final TargetSelector input = Select.allBytes();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, byte.class, Byte.class);
    }

    @Test
    void allFloats() {
        final TargetSelector input = Select.allFloats();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, float.class, Float.class);
    }

    @Test
    void allShorts() {
        final TargetSelector input = Select.allShorts();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, short.class, Short.class);
    }

    @Test
    void allInts() {
        final TargetSelector input = Select.allInts();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, int.class, Integer.class);
    }

    @Test
    void allLongs() {
        final TargetSelector input = Select.allLongs();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, long.class, Long.class);
    }

    @Test
    void allDoubles() {
        final TargetSelector input = Select.allDoubles();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, double.class, Double.class);
    }

    @Test
    void allBooleans() {
        final TargetSelector input = Select.allBooleans();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, boolean.class, Boolean.class);
    }

    @Test
    void allChars() {
        final TargetSelector input = Select.allChars();

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertPrimitiveWrapperPair(results, char.class, Character.class);
    }

    @Test
    void coreTypeSelectorWithScope() {
        final TargetSelector input = Select.allChars()
                .within(Select.scope(Foo.class),
                        Select.scope(Bar.class, "barValue"));

        final List<TargetSelector> results = processor.process(input, ApiMethodSelector.NONE);

        assertThat(results).hasSize(2).allSatisfy(selector -> {
            assertSelector(selector).hasScopeSize(2);

            assertScope(((SelectorImpl) selector).getScopes().get(0))
                    .hasTargetClass(Foo.class);

            assertScope(((SelectorImpl) selector).getScopes().get(1))
                    .hasTargetClass(Bar.class)
                    .hasField("barValue");
        });
    }

    private static void assertPrimitiveWrapperPair(
            final List<TargetSelector> results,
            final Class<?> primitiveClass,
            final Class<?> wrapperClass) {

        assertThat(results).hasSize(2);

        assertSelector(results.get(0))
                .isClassSelectorWithNoScope()
                .hasTargetClass(primitiveClass);

        assertSelector(results.get(1))
                .isClassSelectorWithNoScope()
                .hasTargetClass(wrapperClass);
    }
}
