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
package org.instancio.test.features.generator.custom;

import org.instancio.Instancio;
import org.instancio.generator.Generator;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.immutable.FooBarBazInterface;
import org.instancio.test.support.pojo.immutable.ImmutableFooBarBaz;
import org.instancio.test.support.pojo.immutable.ImmutableLombokFooBarBaz;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.Select.types;

@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class CustomGeneratorWithImmutableObjectTest {

    @Test
    @DisplayName("Create immutable object with one null field; update field to a non-null value via set()")
    void immutableFooBarBaz() {
        assertFooBarBaz(
                ImmutableFooBarBaz.class,
                random -> new ImmutableFooBarBaz("FOO", null, null));

        assertFooBarBaz(
                ImmutableLombokFooBarBaz.class,
                random -> ImmutableLombokFooBarBaz.builder().foo("FOO").bar(null).baz(null).build());
    }

    private void assertFooBarBaz(final Class<? extends FooBarBazInterface> klass,
                                 final Generator<?> generator) {

        final FooBarBazInterface result = Instancio.of(klass)
                .supply(types().of(FooBarBazInterface.class), generator)
                .set(field("bar"), "BAR")
                .create();

        assertThat(result.getFoo()).isEqualTo("FOO");
        assertThat(result.getBar()).isEqualTo("BAR");
        assertThat(result.getBaz()).isNotNull();
    }

}
