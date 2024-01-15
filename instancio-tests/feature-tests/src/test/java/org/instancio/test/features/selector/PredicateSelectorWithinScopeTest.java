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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.SCOPE})
@ExtendWith(InstancioExtension.class)
class PredicateSelectorWithinScopeTest {

    private static Stream<Arguments> selectors() {
        return Stream.of(
                // types
                Arguments.of(types(t -> t == String.class).within(scope(StringsDef.class))),
                Arguments.of(types(t -> t == String.class).within(all(StringsDef.class).toScope())),
                Arguments.of(types(t -> t == String.class).within(scope(StringsAbc::getDef))),
                Arguments.of(types(t -> t == String.class).within(scope(StringsAbc.class), scope(StringsAbc::getDef))),
                Arguments.of(types(t -> t == String.class).within(scope(StringsAbc.class), scope(StringsDef.class))),
                // fields
                Arguments.of(fields(f -> f.getType() == String.class).within(scope(StringsAbc::getDef))),
                Arguments.of(fields(f -> f.getType() == String.class).within(all(StringsDef.class).toScope())),
                Arguments.of(fields(f -> f.getType() == String.class).within(field("def").toScope())),
                Arguments.of(fields(f -> f.getType() == String.class).within(field(StringsAbc::getDef).toScope()))
        );
    }

    @MethodSource("selectors")
    @ParameterizedTest
    void withinScope(final TargetSelector selector) {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .set(selector, "foo")
                .create();

        assertThat(result.getA()).isNotEqualTo("foo");
        assertThat(result.getB()).isNotEqualTo("foo");
        assertThat(result.getC()).isNotEqualTo("foo");

        assertThatObject(result.getDef()).hasAllFieldsOfTypeEqualTo(String.class, "foo");
        assertThatObject(result.getDef().getGhi()).hasAllFieldsOfTypeEqualTo(String.class, "foo");
    }

}
