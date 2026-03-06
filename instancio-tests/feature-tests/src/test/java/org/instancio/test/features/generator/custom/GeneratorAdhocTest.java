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
package org.instancio.test.features.generator.custom;

import lombok.Data;
import org.assertj.core.api.Condition;
import org.instancio.Instancio;
import org.instancio.Random;
import org.instancio.generator.AfterGenerate;
import org.instancio.generator.Generator;
import org.instancio.generator.Hints;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.basic.StringHolderAlternativeImpl;
import org.instancio.test.support.pojo.interfaces.StringHolderInterface;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.tags.NonDeterministicTag;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.field;
import static org.instancio.Select.fields;

@FeatureTag({
        Feature.GENERATOR,
        Feature.IGNORE,
        Feature.AFTER_GENERATE,
        Feature.STREAM,
        Feature.SUBTYPE,
        Feature.WITH_NULLABLE
})
@NonDeterministicTag
@ExtendWith(InstancioExtension.class)
class GeneratorAdhocTest {

    private static final int COLLECTION_SIZE = 250;

    private final Generator<StringFields> stringFieldsGenerator = new Generator<StringFields>() {
        @Override
        public StringFields generate(final Random random) {
            // remaining fields are null
            return StringFields.builder().one("one").build();
        }

        @Override
        public Hints hints() {
            return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS);
        }
    };

    @Test
    @DisplayName("withNullable() can be specified for object created by custom generator")
    void withNullable() {
        final List<StringFields> results = Instancio.of(StringFields.class)
                .supply(all(StringFields.class), stringFieldsGenerator)
                .withNullable(fields().annotated(StringFields.Two.class))
                .stream()
                .limit(COLLECTION_SIZE)
                .collect(Collectors.toList());

        assertThat(results).hasSize(COLLECTION_SIZE).allSatisfy(result -> {
            assertThat(result.getOne()).isEqualTo("one");
            assertThat(result.getThree()).isNotNull();
            assertThat(result.getFour()).isNotNull();
        });

        assertThat(results)
                .as("Should contain null and non-null values")
                .extracting(StringFields::getTwo)
                .areAtLeastOne(new Condition<>(Objects::nonNull, "Expected at least one non-null"))
                .containsNull();
    }

    @Test
    @DisplayName("ignore() can be specified for object created by custom generator")
    void ignore() {
        final List<StringFields> results = Instancio.of(StringFields.class)
                .supply(all(StringFields.class), stringFieldsGenerator)
                .ignore(fields().annotated(StringFields.Two.class))
                .stream()
                .limit(COLLECTION_SIZE)
                .collect(Collectors.toList());

        assertThat(results).hasSize(COLLECTION_SIZE).allSatisfy(result -> {
            assertThat(result.getOne()).isEqualTo("one");
            assertThat(result.getThree()).isNotNull();
            assertThat(result.getFour()).isNotNull();
        });

        assertThat(results).extracting(StringFields::getTwo)
                .as("Should contain only nulls since field is ignored")
                .containsOnlyNulls();
    }

    @Test
    @DisplayName("subtype() can be specified for object created by custom generator")
    void subtype() {
        @Data
        class Container {
            @Nullable StringHolderInterface holderOne;
            @Nullable StringHolderInterface holderTwo;
        }

        final String expectedHolderOneValue = "one";
        final Container result = Instancio.of(Container.class)
                .subtype(all(StringHolderInterface.class), StringHolder.class)
                // Need to specify the subtype of holderOne explicitly to match
                // the class in the custom generator so that we can resolve the fields
                .subtype(field(Container::getHolderOne), StringHolderAlternativeImpl.class)
                .supply(all(Container.class), new Generator<Container>() {
                    @Override
                    public Container generate(final Random random) {
                        final Container container = new Container();
                        container.holderOne = new StringHolderAlternativeImpl(expectedHolderOneValue);
                        // since it's null, it should be generated by the engine,
                        // and we should also be able to specify its subtype
                        container.holderTwo = null;
                        return container;
                    }

                    @Override
                    public Hints hints() {
                        return Hints.afterGenerate(AfterGenerate.POPULATE_NULLS);
                    }
                })
                .create();

        assertThat(result.holderOne)
                .as("Should be of type created by the custom generator")
                .isExactlyInstanceOf(StringHolderAlternativeImpl.class)
                .extracting(StringHolderInterface::getValue)
                .isEqualTo(expectedHolderOneValue);

        assertThat(result.holderTwo)
                .as("Should be of type specified via the subtype() method")
                .isExactlyInstanceOf(StringHolder.class)
                .extracting(StringHolderInterface::getValue)
                .isNotNull();
    }
}
