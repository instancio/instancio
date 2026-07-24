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
package org.instancio.test.features.instantiation;

import org.instancio.Instancio;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.constructor.StringsAbcCtor;
import org.instancio.test.support.pojo.constructor.StringsDefCtor;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.instancio.Assign.valueOf;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

/**
 * Verifies that {@code elementOf()} selectors reach elements that are
 * instantiated via constructor, including the case where an element's
 * constructor argument depends on a later element. Since the argument
 * is unavailable when the element is first visited, the element cannot
 * be constructed and is retried once the origin exists.
 *
 * @see ConstructorSelectorsTest
 */
@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.INSTANTIATION_STRATEGIES, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class ConstructorElementOfSelectorTest {

    private static final int SIZE = 4;
    private static final int LAST = SIZE - 1;
    private static final String EXPECTED_STRING = "_value_";

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    private record ListHolder(List<StringsAbcCtor> elements) {}

    private record ElementAndSibling(List<StringsAbcCtor> elements, StringsAbcCtor sibling) {}

    @Test
    void setConstructorParameterOfElementAtIndex() {
        final ListHolder result = Instancio.of(ListHolder.class)
                .set(elementOf(ListHolder::elements).at(1).field(StringsAbcCtor::getA), EXPECTED_STRING)
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "elements[1].a");
    }

    @Test
    void ignoredConstructorParameterOfElementAtIndex() {
        final ListHolder result = Instancio.of(ListHolder.class)
                .ignore(elementOf(ListHolder::elements).at(0).field(StringsAbcCtor::getA))
                .create();

        assertThatGraph(result).hasValuesOfTypeEqualToExactlyIn(String.class, null, "elements[0].a");
    }

    /**
     * The origin is the last element, so the destination element cannot
     * be constructed when it is first visited.
     */
    @Test
    void assignConstructorParameterFromLastElement() {
        final TargetSelector origin = elementOf(ListHolder::elements).at(LAST).field(StringsAbcCtor::getA);
        final TargetSelector destination = elementOf(ListHolder::elements).at(0).field(StringsAbcCtor::getB);

        final ListHolder result = Instancio.of(ListHolder.class)
                .set(origin, EXPECTED_STRING)
                .assign(valueOf(origin).to(destination))
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                "elements[%s].a".formatted(LAST),
                "elements[0].b",
        });
    }

    @Test
    void assignConstructorParameterAcrossAllOtherElements() {
        final TargetSelector origin = elementOf(ListHolder::elements).at(LAST).field(StringsAbcCtor::getA);
        final TargetSelector destination = elementOf(ListHolder::elements).except(LAST).field(StringsAbcCtor::getA);

        final ListHolder result = Instancio.of(ListHolder.class)
                .set(origin, EXPECTED_STRING)
                .assign(valueOf(origin).to(destination))
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, "elements[*].a");
    }

    /**
     * The whole element is assigned from a sibling that is generated
     * after the collection, deferring the element rather than one of
     * its constructor arguments.
     */
    @Test
    void assignWholeElementFromLaterSibling() {
        final ElementAndSibling result = Instancio.of(ElementAndSibling.class)
                .assign(valueOf(ElementAndSibling::sibling).to(elementOf(ElementAndSibling::elements)))
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(result.sibling(), new String[]{
                "elements[*]",
                "sibling",
        });
    }

    /**
     * The destination is nested inside an element's constructor argument.
     */
    @Test
    void assignNestedConstructorParameterOfElement() {
        final TargetSelector origin = elementOf(ListHolder::elements).at(LAST).field(StringsAbcCtor::getA);
        final TargetSelector destination = elementOf(ListHolder::elements).at(0)
                .target(field(StringsDefCtor::getD));

        final ListHolder result = Instancio.of(ListHolder.class)
                .set(origin, EXPECTED_STRING)
                .assign(valueOf(origin).to(destination))
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn(EXPECTED_STRING, new String[]{
                "elements[%s].a".formatted(LAST),
                "elements[0].def.d",
        });
    }
}
