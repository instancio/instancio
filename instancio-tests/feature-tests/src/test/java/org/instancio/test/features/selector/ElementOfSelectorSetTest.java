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
package org.instancio.test.features.selector;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.collections.sets.SetInteger;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;

@FeatureTag(Feature.ELEMENT_OF_SELECTOR)
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorSetTest {

    @Test
    void withSet() {
        final SetInteger result = Instancio.of(SetInteger.class)
                .set(elementOf(SetInteger::getSet).first(), -1)
                .create();

        assertThat(result.getSet())
                .hasSizeGreaterThan(1)
                .contains(-1);
    }

    /**
     * Setting a sub-field on every element via {@code set()} is supported on a Set:
     * it is not an assignment, applies the same value to each element before insertion,
     * and has no positional dependency. This is the documented alternative to the
     * unsupported {@code assign()}-based usages below.
     */
    @Test
    void setSubFieldOnAllElements() {
        record SetHolder(Set<StringsAbc> set) {}

        final SetHolder result = Instancio.of(SetHolder.class)
                .set(elementOf(field(SetHolder::set)).field(StringsAbc::getA), "xxx")
                .create();

        assertThat(result.set())
                .isNotEmpty()
                .allSatisfy(element -> assertThat(element.getA()).isEqualTo("xxx"));
    }

    @Nested
    class Unsupported {

        @FeatureTag(Feature.UNSUPPORTED)
        @Test
        void assignGenerateOnSet() {
            final InstancioApi<SetInteger> api = Instancio.of(SetInteger.class)
                    // NOTE: this works with generate, e.g.
                    // .generate(elementOf(SetInteger::getSet), gen -> gen.ints().range(-10, -1))
                    .assign(Assign.valueOf(elementOf(SetInteger::getSet))
                            .generate(gen -> gen.ints().range(-10, -1)));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() in assign() requires an ordered, index-addressable container")
                    .hasMessageContaining("elementOf(SetInteger::getSet)");
        }

        @FeatureTag(Feature.UNSUPPORTED)
        @Test
        void assignSetValueAtIndexOnSet() {
            // valueOf(X).set(v) is an unconditional value assignment, equivalent to set(X, v) -
            // which IS supported on a Set. But elementOf() in assign() is unsupported regardless;
            // the error directs the user to the equivalent set(elementOf(set).at(1), -1).
            final InstancioApi<SetInteger> api = Instancio.of(SetInteger.class)
                    .assign(Assign.valueOf(elementOf(SetInteger::getSet).at(1)).set(-1));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() in assign() requires an ordered, index-addressable container")
                    .hasMessageContaining("elementOf(SetInteger::getSet).at(1)");
        }

        @FeatureTag(Feature.UNSUPPORTED)
        @Test
        void assignWholeElementCopyOnSet() {
            final InstancioApi<SetInteger> api = Instancio.of(SetInteger.class)
                    .assign(Assign.valueOf(elementOf(SetInteger::getSet).first())
                            .to(elementOf(SetInteger::getSet).last()));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() in assign() requires an ordered, index-addressable container")
                    .hasMessageContaining("elementOf(SetInteger::getSet)");
        }

        /**
         * {@code lenient()} suppresses unused-selector reporting, but elementOf() in assign()
         * on a Set can never work, so it is a hard error regardless of leniency.
         */
        @FeatureTag(Feature.UNSUPPORTED)
        @Test
        void lenientAssignWholeElementCopyOnSet_stillReported() {
            final InstancioApi<SetInteger> api = Instancio.of(SetInteger.class)
                    .assign(Assign.valueOf(elementOf(SetInteger::getSet).first())
                            .to(elementOf(SetInteger::getSet).last().lenient()));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() in assign() requires an ordered, index-addressable container");
        }

        @FeatureTag(Feature.UNSUPPORTED)
        @Test
        void assignSubElementOnSet() {
            record SetHolder(Set<StringsAbc> set) {}

            final InstancioApi<SetHolder> api = Instancio.of(SetHolder.class)
                    .assign(Assign.valueOf(field(StringsAbc::getA))
                            .to(elementOf(field(SetHolder::set)).first().field(StringsAbc::getB)));

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioApiException.class)
                    .hasMessageContaining("elementOf() in assign() requires an ordered, index-addressable container")
                    .hasMessageContaining("elementOf(field(SetHolder::set)).first().field(StringsAbc::getB)");
        }
    }
}
