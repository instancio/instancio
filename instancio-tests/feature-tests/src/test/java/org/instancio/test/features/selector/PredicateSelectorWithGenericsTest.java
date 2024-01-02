/*
 *  Copyright 2022-2024 the original author or authors.
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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.GenericsTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;

@GenericsTag
@FeatureTag(Feature.SELECTOR)
class PredicateSelectorWithGenericsTest {

    private static final String EXPECTED_STRING = "foo";

    @Test
    @DisplayName("Predicate Field selector matches using generic type")
    void fieldSelectorMatchesUsingGenericType() {
        final Foo<String> result = Instancio.of(new TypeToken<Foo<String>>() {})
                .set(fields(f -> f.getGenericType() == Foo.class.getTypeParameters()[0]), EXPECTED_STRING)
                .create();

        assertThat(result.getFooValue()).isEqualTo(EXPECTED_STRING);
    }

    @Test
    @DisplayName("Predicate Field selector does NOT match TypeVariable by type")
    void fieldSelectorDoesNotMatchTypeVariableFieldByType() {
        final Foo<String> result = Instancio.of(new TypeToken<Foo<String>>() {})
                // Generic field getType() returns Object.class, so it doesn't match
                .set(fields().ofType(String.class), EXPECTED_STRING)
                .lenient() // since field type does not match, unused selector error is triggered
                .create();

        assertThat(result.getFooValue()).isNotEqualTo(EXPECTED_STRING);
    }

    @Test
    @DisplayName("Predicate Type selector should match TypeVariable field by type")
    void typeSelectorShouldMatchTypeVariableByClass() {
        final Item<String> result = Instancio.of(new TypeToken<Item<String>>() {})
                .set(types().of(String.class), EXPECTED_STRING)
                .create();

        assertThat(result.getValue()).isEqualTo(EXPECTED_STRING);
    }

    @Test
    @DisplayName("Predicate Field selector should match TypeVariable field by field name")
    void fieldSelectorShouldMatchTypeVariableByFieldName() {
        final Item<String> result = Instancio.of(new TypeToken<Item<String>>() {})
                .set(fields().named("value"), EXPECTED_STRING)
                .create();

        assertThat(result.getValue()).isEqualTo(EXPECTED_STRING);
    }
}