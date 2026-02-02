/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.features.generator.string;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.arrays.object.WithStringArray;
import org.instancio.test.support.pojo.collections.lists.ListString;
import org.instancio.test.support.pojo.collections.maps.MapIntegerString;
import org.instancio.test.support.pojo.collections.maps.MapStringPerson;
import org.instancio.test.support.pojo.misc.OptionalString;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.test.support.asserts.Asserts.assertDoesNotHaveFieldPrefix;

/**
 * Strings that are not fields, e.g. collection/array elements, map keys, etc.
 * should not be prefixed with field name.
 */
@FeatureTag({Feature.STRING_GENERATOR, Feature.STRING_FIELD_PREFIX})
@ExtendWith(InstancioExtension.class)
class StringFieldPrefixDataStructuresTest {

    private static final String DEFAULT_STRING_PATTERN = "^[A-Z]+$";

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.STRING_FIELD_PREFIX_ENABLED, true);

    @Test
    void optional() {
        final String fieldName = "optional";
        final OptionalString result = Instancio.create(OptionalString.class);

        final Optional<String> optional = result.getOptional();
        assertThat(optional).isPresent();
        assertDoesNotHaveFieldPrefix(fieldName, optional.get()).matches(DEFAULT_STRING_PATTERN);
    }

    @Test
    void arrayElements() {
        final String fieldName = "values";
        final WithStringArray result = Instancio.create(WithStringArray.class);

        assertThat(result.getClass()).hasDeclaredFields(fieldName);
        assertNoPrefix(fieldName, Arrays.asList(result.getValues()));
    }

    @Test
    void listElements() {
        final String fieldName = "list";
        final ListString result = Instancio.create(ListString.class);

        assertThat(result.getClass()).hasDeclaredFields(fieldName);
        assertNoPrefix(fieldName, result.getList());
    }

    @Test
    void mapKeys() {
        final String fieldName = "map";
        final MapStringPerson result = Instancio.create(MapStringPerson.class);

        assertThat(result.getClass()).hasDeclaredFields(fieldName);
        assertNoPrefix(fieldName, result.getMap().keySet());
    }

    @Test
    void mapValues() {
        final String fieldName = "map";
        final MapIntegerString result = Instancio.create(MapIntegerString.class);

        assertThat(result.getClass()).hasDeclaredFields(fieldName);
        assertNoPrefix(fieldName, result.getMap().values());
    }

    private static void assertNoPrefix(final String fieldName, final Collection<String> actual) {
        assertThat(actual).isNotEmpty().allSatisfy(s ->
                assertDoesNotHaveFieldPrefix(fieldName, s).matches(DEFAULT_STRING_PATTERN));
    }
}
