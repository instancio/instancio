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

import org.instancio.ElementOfSelector;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.exception.UnresolvedAssignmentException;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.arrays.primitive.WithIntArray;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Assign.valueOf;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

/**
 * Tests for delayed cross-element resolution paths, where the assignment origin
 * sits at a position generated after the destination. When the destination is
 * generated, the origin value does not exist yet, so the destination element
 * returns a "delayed" result and is resolved on a later pass.
 */
@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.ASSIGN})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorDelayedResolutionTest {

    private static final int SIZE = 5;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    /**
     * A primitive array cannot park a null placeholder in a delayed slot,
     * so a backward cross-element copy (origin index to destination index)
     * within the same primitive array can never be satisfied: regenerating
     * the array always re-encounters the destination before the origin.
     * This is reported as an unresolved assignment rather than looping forever.
     */
    @FeatureTag(Feature.UNSUPPORTED)
    @Test
    void primitiveArray_backwardCrossElementAssign_isUnresolved() {
        final InstancioApi<WithIntArray> api = Instancio.of(WithIntArray.class)
                .set(elementOf(WithIntArray::getValues).at(2), 999)
                .assign(valueOf(elementOf(WithIntArray::getValues).at(2))
                        .to(elementOf(WithIntArray::getValues).at(0)));

        assertThatThrownBy(api::create)
                .isInstanceOf(UnresolvedAssignmentException.class);
    }

    @Test
    void objectArray_backwardCopyOfNullOrigin_leavesDestinationNull() {
        final ElementOfSelector elementSelector = elementOf(field(AbcArrayHolder::getAbcElements1));

        final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                .set(elementSelector.at(2), null)
                .assign(valueOf(elementSelector.at(2)).to(elementSelector.at(0)))
                .create();

        assertThat(result.abcElements1).hasSize(SIZE);
        assertThat(result.abcElements1[0]).isNull();
    }

    @Test
    void set_wholeElementAssignedFromLaterSibling_resolvesAfterDeferral() {
        record SetSiblingHolder(Set<StringsAbc> set, StringsAbc origin) {}

        final SetSiblingHolder result = Instancio.of(SetSiblingHolder.class)
                .assign(valueOf(SetSiblingHolder::origin)
                        .to(all(StringsAbc.class).within(field(SetSiblingHolder::set).toScope())))
                .create();

        assertThat(result.set).containsOnly(result.origin);
    }

    @Test
    void delayedCrossElement_placesExplicitNullsIntoNonNullableList() {
        final TargetSelector origin = elementOf(AbcListHolder::getAbcElements1).at(2);
        final TargetSelector destination = elementOf(AbcListHolder::getAbcElements1).at(0, 3);

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .withSetting(Keys.COLLECTION_ELEMENTS_NULLABLE, false)
                .size(field(AbcListHolder::getAbcElements1), SIZE)
                .set(origin, null)
                .assign(valueOf(origin).to(destination))
                .create();

        assertThat(result.getAbcElements1()).hasSize(SIZE);
        assertThatGraph(result)
                .hasValuesOfTypeEqualToExactlyIn(StringsAbc.class, null, "abcElements1[0,2,3]");
    }

    @Test
    void crossElementAssignmentShouldNotLeakValuesAcrossContainerInstances() {
        final TargetSelector origin = elementOf(AbcListHolder::getAbcElements1)
                .at(2) // at
                .field(StringsAbc::getA);

        final TargetSelector destination = elementOf(AbcListHolder::getAbcElements1)
                .except(2) // except
                .field(StringsAbc::getA);

        final List<AbcListHolder> results = Instancio.ofList(AbcListHolder.class)
                .assign(valueOf(origin).to(destination))
                .create();

        for (int i = 0; i < results.size(); i++) {
            final List<StringsAbc> elements = results.get(i).getAbcElements1();
            final String originValue = elements.get(2).getA();

            assertThat(elements)
                    .as("container instance [%s]: each element's 'a' must mirror that instance's own origin", i)
                    .extracting(StringsAbc::getA)
                    .containsOnly(originValue);
        }
    }
}
