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
package org.instancio.test.features.generator.custom.collection;

import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.TypeToken;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.generator.hints.CollectionHint;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.generics.inheritance.NonGenericSubclassOfList;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.types;

@FeatureTag({Feature.GENERATOR, Feature.COLLECTION_GENERATOR_SUBTYPE})
@ExtendWith(InstancioExtension.class)
class CustomCollectionWithNonGenericSubclassTest {

    private static final int GENERATE_ELEMENTS = 3;

    private static class CustomListGenerator implements Generator<NonGenericSubclassOfList> {

        @Override
        public NonGenericSubclassOfList generate(final Random random) {
            return new NonGenericSubclassOfList();
        }

        @Override
        public Hints hints() {
            return Hints.builder()
                    .afterGenerate(AfterGenerate.APPLY_SELECTORS)
                    .with(CollectionHint.builder().generateElements(GENERATE_ELEMENTS).build())
                    .build();
        }
    }

    @Test
    void customCollectionCreatedByCustomGenerator() {
        final NonGenericSubclassOfList result = Instancio.of(NonGenericSubclassOfList.class)
                .supply(types().of(List.class), new CustomListGenerator())
                .create();

        assertThat(result)
                .isExactlyInstanceOf(NonGenericSubclassOfList.class)
                .doesNotContainNull()
                .hasSize(GENERATE_ELEMENTS);
    }

    @Test
    void customCollectionCreatedViaSubtype() {
        final Collection<String> result = Instancio.of(new TypeToken<Collection<String>>() {})
                .subtype(types().of(Collection.class), NonGenericSubclassOfList.class)
                .create();

        assertThat(result)
                .isExactlyInstanceOf(NonGenericSubclassOfList.class)
                .isNotEmpty()
                .doesNotContainNull();
    }

    @Test
    void customCollectionCreatedViaGeneratorSubtype() {
        final Collection<String> result = Instancio.of(new TypeToken<Collection<String>>() {})
                .generate(types().of(Collection.class), gen -> gen.collection().subtype(NonGenericSubclassOfList.class))
                .create();

        assertThat(result)
                .isExactlyInstanceOf(NonGenericSubclassOfList.class)
                .isNotEmpty()
                .doesNotContainNull();
    }
}
