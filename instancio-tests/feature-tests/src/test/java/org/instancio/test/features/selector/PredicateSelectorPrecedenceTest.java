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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.Asserts.assertAllNulls;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.PREDICATE_SELECTOR, Feature.SELECTOR_PRECEDENCE,})
@ExtendWith(InstancioExtension.class)
class PredicateSelectorPrecedenceTest {

    @Test
    void lastFieldSelectorWins() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .set(fields().ofType(String.class), "foo") // unused
                .set(fields().ofType(String.class), "bar") // wins
                .lenient()
                .create();

        assertThat(result.getValue()).isEqualTo("bar");
    }

    @Test
    void lastClassSelectorWins() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(types().of(String.class), "foo") // unused
                .set(types().of(String.class), "bar") // wins
                .lenient()
                .create();

        assertThatObject(result).hasAllFieldsOfTypeEqualTo(String.class, "bar");
    }

    @Test
    void fieldSelectorWinsOverClassSelector() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(types().of(String.class), "foo") // unused
                .set(fields().ofType(String.class), "bar") // wins
                .lenient()
                .create();

        assertThatObject(result).hasAllFieldsOfTypeEqualTo(String.class, "bar");
    }

    @Test
    void setAllFieldsNullToAndOverrideSomeFields() {
        final StringFields result = Instancio.of(StringFields.class)
                .set(fields(), null)
                .set(fields().annotated(StringFields.Two.class), "foo")
                .create();

        assertThat(result.getTwo()).isEqualTo("foo");
        assertAllNulls(result.getOne(), result.getThree(), result.getFour());
    }
}
