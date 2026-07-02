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

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.elementOf;
import static org.instancio.test.support.asserts.ObjectGraphAssert.assertThatGraph;

@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.ON_COMPLETE})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorOnCompleteTest {

    private static final int SIZE = 5;

    @WithSettings
    private static final Settings settings = Settings.create()
            .set(Keys.ARRAY_MIN_SIZE, SIZE)
            .set(Keys.ARRAY_MAX_SIZE, SIZE)
            .set(Keys.COLLECTION_MIN_SIZE, SIZE)
            .set(Keys.COLLECTION_MAX_SIZE, SIZE);

    @Test
    void wholeElements_allIndices() {
        final List<StringsAbc> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1),
                        (StringsAbc element) -> collected.add(element))
                .create();

        assertThat(collected).isEqualTo(result.getAbcElements1());
    }

    @Test
    void wholeElement_first() {
        final List<StringsAbc> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).first(),
                        (StringsAbc element) -> collected.add(element))
                .create();

        assertThat(collected).singleElement().isSameAs(result.getAbcElements1().get(0));
    }


    @Test
    void wholeElements_range() {
        final List<StringsAbc> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).range(1, 3),
                        (StringsAbc element) -> collected.add(element))
                .create();

        assertThat(collected).containsExactlyElementsOf(result.getAbcElements1().subList(1, 4));
    }

    @Test
    void widensContainer() {
        final int size = 100;
        final List<StringsAbc> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).at(size - 1),
                        (StringsAbc element) -> collected.add(element))
                .create();

        assertThat(result.getAbcElements1())
                .hasSize(size);

        assertThat(collected)
                .singleElement()
                .isSameAs(result.getAbcElements1().get(size - 1));
    }

    @Test
    void fieldOfFirstElement() {
        final List<String> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA),
                        (String a) -> collected.add(a))
                .create();

        assertThat(collected).singleElement().isEqualTo(result.getAbcElements1().get(0).getA());
    }

    @Test
    void fieldOfAllElements() {
        final List<String> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).field(StringsAbc::getA),
                        (String a) -> collected.add(a))
                .create();

        assertThat(collected).containsExactlyElementsOf(
                result.getAbcElements1().stream().map(StringsAbc::getA).toList());
    }

    @Test
    void targetWithinFirstElement() {
        final List<StringsDef> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).first().target(all(StringsDef.class)),
                        (StringsDef def) -> collected.add(def))
                .create();

        assertThat(collected).singleElement().isSameAs(result.getAbcElements1().get(0).getDef());
    }

    @Test
    void callbackObservesValuesCustomisedViaSelectors() {
        final List<String> collected = new ArrayList<>();

        Instancio.of(AbcListHolder.class)
                .set(elementOf(AbcListHolder::getAbcElements1).first().field(StringsAbc::getA), "expected-a")
                .onComplete(elementOf(AbcListHolder::getAbcElements1).first(),
                        (StringsAbc element) -> collected.add(element.getA()))
                .create();

        assertThat(collected).containsExactly("expected-a");
    }

    /**
     * Callbacks run after the object has been fully assembled,
     * so mutations made by the callback are visible in the result.
     */
    @Test
    void callbackCanMutateElement() {
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).first(),
                        (StringsAbc element) -> element.setA("modified"))
                .create();

        assertThatGraph(result).hasValuesEqualToExactlyIn("modified", "abcElements1[0].a");
    }

    @Test
    void groupOfElementOfSelectors() {
        final List<StringsAbc> collected = new ArrayList<>();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(all(
                                elementOf(AbcListHolder::getAbcElements1),
                                elementOf(AbcListHolder::getAbcElements2)),
                        (StringsAbc element) -> collected.add(element))
                .create();

        assertThat(collected)
                .hasSize(SIZE * 2)
                .containsAll(result.getAbcElements1())
                .containsAll(result.getAbcElements2());
    }

    @Test
    void onCompleteDefinedInModel() {
        final AtomicInteger invocations = new AtomicInteger();

        final Model<AbcListHolder> model = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).first(),
                        (StringsAbc element) -> invocations.incrementAndGet())
                .toModel();

        Instancio.create(model);
        assertThat(invocations).hasValue(1);

        Instancio.create(model);
        assertThat(invocations).hasValue(2);
    }

    @Test
    void arrayContainer() {
        final List<StringsAbc> collected = new ArrayList<>();

        final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                .onComplete(elementOf(AbcArrayHolder::getAbcElements1).first(),
                        (StringsAbc element) -> collected.add(element))
                .create();

        assertThat(collected).singleElement().isSameAs(result.getAbcElements1()[0]);
    }

    /**
     * {@code except()} does not widen the container, so excluding all
     * indices results in no matches; {@code lenient()} suppresses the
     * unused-selector error.
     */
    @Test
    void lenientSelectorWithNoMatchingElements() {
        final AtomicInteger invocations = new AtomicInteger();

        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .onComplete(elementOf(AbcListHolder::getAbcElements1).except(0, 1, 2, 3, 4).lenient(),
                        (StringsAbc element) -> invocations.incrementAndGet())
                .create();

        assertThat(result).isNotNull();
        assertThat(invocations).hasValue(0);
    }
}
