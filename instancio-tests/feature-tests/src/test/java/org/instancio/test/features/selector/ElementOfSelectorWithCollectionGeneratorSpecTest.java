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
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.AbcArrayHolder;
import org.instancio.test.support.pojo.misc.AbcListHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.elementOf;
import static org.instancio.Select.field;

/**
 * NOTE: using {@code with()} method shuffles collection elements,
 * therefore we cannot assert element position when using {@code elementOf()}
 * with specific indices.
 */
@FeatureTag({Feature.ELEMENT_OF_SELECTOR, Feature.SELECTOR, Feature.COLLECTION_GENERATOR_WITH_ELEMENTS})
@ExtendWith(InstancioExtension.class)
class ElementOfSelectorWithCollectionGeneratorSpecTest {

    private static final StringsAbc ABC_VIA_ELEMENT_OF = StringsAbc.builder().a("_viaElementOf_").build();
    private static final StringsAbc ABC_VIA_WITH = StringsAbc.builder().a("_viaWith_").build();

    private final int collectionSpecSize = Instancio.gen().ints().range(1, 10).get();
    private final int expectedCollectionSize = collectionSpecSize + 1; // +1 to include with() element

    @Test
    void collectionLastAppliesToGeneratedElementAndPreservesWithElement() {
        final AbcListHolder result = Instancio.of(AbcListHolder.class)
                .generate(field(AbcListHolder::getAbcElements1),
                        gen -> gen.collection().size(collectionSpecSize).with(ABC_VIA_WITH))
                .set(elementOf(AbcListHolder::getAbcElements1).last(), ABC_VIA_ELEMENT_OF)
                .create();

        assertThat(result.getAbcElements1())
                .hasSize(expectedCollectionSize)
                .contains(ABC_VIA_ELEMENT_OF, ABC_VIA_WITH);
    }

    @Test
    void arrayFirstAppliesToGeneratedElementAndPreservesWithElement() {
        final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                .generate(field(AbcArrayHolder::getAbcElements1),
                        gen -> gen.array().size(collectionSpecSize).with(ABC_VIA_WITH))
                .set(elementOf(AbcArrayHolder::getAbcElements1).first(), ABC_VIA_ELEMENT_OF)
                .create();

        assertThat(result.getAbcElements1())
                .hasSize(expectedCollectionSize)
                .contains(ABC_VIA_ELEMENT_OF, ABC_VIA_WITH);
    }

    @Test
    void arrayLastAppliesToGeneratedElementAndPreservesWithElement() {
        final AbcArrayHolder result = Instancio.of(AbcArrayHolder.class)
                .generate(field(AbcArrayHolder::getAbcElements1),
                        gen -> gen.array().size(collectionSpecSize).with(ABC_VIA_WITH))
                .set(elementOf(AbcArrayHolder::getAbcElements1).last(), ABC_VIA_ELEMENT_OF)
                .create();

        assertThat(result.getAbcElements1())
                .hasSize(expectedCollectionSize)
                .contains(ABC_VIA_ELEMENT_OF, ABC_VIA_WITH);
    }
}