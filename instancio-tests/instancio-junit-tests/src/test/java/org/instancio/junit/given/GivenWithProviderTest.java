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
package org.instancio.junit.given;

import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.generator.specs.ArrayGeneratorSpec;
import org.instancio.generator.specs.CollectionGeneratorSpec;
import org.instancio.junit.Given;
import org.instancio.junit.GivenProvider;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.InstancioSource;
import org.instancio.settings.Keys;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class GivenWithProviderTest {

    @CollectionSize(5)
    private @Nullable Set<String> setWithSize;

    @NullableElements
    @CollectionSize(100)
    private @Nullable List<String> listWithSizeNullableElements;

    @Test
    void verifyFields() {
        assertThat(setWithSize).hasSize(5);

        assertThat(listWithSizeNullableElements).hasSize(100).containsNull();
    }

    @Test
    void verifyParameter(@CollectionSize(10) final List<String> listWithSize) {
        assertThat(listWithSize).hasSize(10);
    }

    @InstancioSource(samples = 5)
    @ParameterizedTest
    void verifyParameterizedTest(@CollectionSize(10) final List<String> listWithSize) {
        assertThat(listWithSize).hasSize(10);
    }

    @Test
    void verifyMultipleParameters(
            @CollectionSize(1) final List<String> collectionSize1,
            @CollectionSize(2) @UnrelatedAnnotation final List<String> collectionSize2,
            @UnrelatedAnnotation final Set<String> collectionWithoutSize,
            @CollectionSize(100) @NullableElements final List<String> collectionSize100AndNullableElements,
            @Given @ArrayLength(100) @NullableElements final String[] arrayLength100AndNullableElements,
            @CollectionSize(4) final Set<String> collectionSize4,
            @CollectionSize(8) final Queue<String> collectionSize8) {

        assertThat(collectionSize1).hasSize(1).doesNotContainNull();
        assertThat(collectionSize2).hasSize(2).doesNotContainNull();
        assertThat(collectionWithoutSize).hasSizeBetween(
                        Keys.COLLECTION_MIN_SIZE.defaultValue(),
                        Keys.COLLECTION_MAX_SIZE.defaultValue())
                .doesNotContainNull();
        assertThat(collectionSize100AndNullableElements).hasSize(100).containsNull();
        assertThat(arrayLength100AndNullableElements).hasSize(100).containsNull();
        assertThat(collectionSize4).hasSize(4).doesNotContainNull();
        assertThat(collectionSize8).hasSize(8).doesNotContainNull();
    }

    @Test
    void parameterWithGivenAnnotation(@Given(NumericStringProvider.class) final String numericString) {
        assertThat(numericString).containsOnlyDigits();
    }

    // should be ignored
    @Given
    @Retention(RetentionPolicy.RUNTIME)
    private @interface UnrelatedAnnotation {}

    @Retention(RetentionPolicy.RUNTIME)
    private @interface NullableElements {}

    @Given(CollectionProvider.class)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface CollectionSize {
        int value();
    }

    // This annotation is not annotated with @Given, therefore,
    // @Given must be specified wherever @ArrayLength is used
    @Given(ArrayProvider.class)
    @Retention(RetentionPolicy.RUNTIME)
    private @interface ArrayLength {
        int value();
    }

    private static class NumericStringProvider implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            // using provided Random
            return context.random().digits(10);
        }
    }

    private static class CollectionProvider implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            CollectionSize size = context.getAnnotation(CollectionSize.class);
            boolean nullableElements = context.getAnnotation(NullableElements.class) != null;

            return Instancio.of(context::getTargetType)
                    .generate(Select.root(), gen -> {
                        CollectionGeneratorSpec<Object> spec = gen.collection().size(size.value());
                        return nullableElements ? spec.nullableElements() : spec;
                    })
                    .create();
        }
    }

    private static class ArrayProvider implements GivenProvider {
        @Override
        public Object provide(final ElementContext context) {
            ArrayLength size = context.getAnnotation(ArrayLength.class);
            boolean nullableElements = context.getAnnotation(NullableElements.class) != null;

            return Instancio.of(context::getTargetType)
                    .generate(Select.root(), gen -> {
                        ArrayGeneratorSpec<Object> spec = gen.array().length(size.value());
                        return nullableElements ? spec.nullableElements() : spec;
                    })
                    .create();
        }
    }

}
