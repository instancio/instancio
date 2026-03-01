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
package org.instancio.test.features.blank;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioException;
import org.instancio.generators.Generators;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.nested.OuterClass;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.jspecify.annotations.NullUnmarked;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.scope;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * Tests for creating a blank root object using:
 *
 * <ul>
 *   <li>{@link Instancio#createBlank(Class)}</li>
 *   <li>{@link Instancio#ofBlank(Class)}</li>
 * </ul>
 */
@FeatureTag(Feature.BLANK)
@ExtendWith(InstancioExtension.class)
class BlankRootObjectTest {

    /**
     * Tests for {@link Instancio#createBlank(Class)}.
     */
    @Nested
    class CreateBlankTest {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.FAIL_ON_ERROR, true);

        @Test
        void rootObjectIsASimpleValueType() {
            assertThat(Instancio.createBlank(String.class)).isNotBlank();
            assertThat(Instancio.createBlank(Integer.class)).isNotNull();
        }

        @Test
        void rootObjectIsAPojo() {
            final Pojo result = Instancio.createBlank(Pojo.class);

            assertThat(result.string).isNull();
            assertThat(result.primitive).isZero();
            assertThat(result.optionalStringHolder).isNotEmpty();
            assertThat(result.optionalStringHolder.get().getValue()).isNull();
            assertThat(result.optionalString).isEmpty();
            assertThat(result.listString).isEmpty();
            assertThat(result.listStringHolder).isEmpty();
            assertThat(result.mapStringHolderLong).isEmpty();
            assertThat(result.mapLongStringHolder).isEmpty();
            assertThat(result.arrayString).isEmpty();
            assertThat(result.arrayStringHolder).isEmpty();
            assertThatObject(result.stringsAbc).hasAllFieldsOfTypeSetToNull(String.class);
            assertThatObject(result.stringsAbc.def).hasAllFieldsOfTypeSetToNull(String.class);
            assertThatObject(result.stringsAbc.def.ghi).hasAllFieldsOfTypeSetToNull(String.class);
        }
    }

    /**
     * Tests for {@link Instancio#ofBlank(Class)}.
     */
    @Nested
    class OfBlankTest {
        @WithSettings
        private final Settings settings = Settings.create()
                .set(Keys.FAIL_ON_ERROR, true);

        @Test
        void overrideArrayLength() {
            final int length = 5;
            final Pojo result = Instancio.ofBlank(Pojo.class)
                    .generate(field(Pojo::getArrayString), gen -> gen.array().length(length))
                    .generate(field(Pojo::getArrayStringHolder), gen -> gen.array().length(length))
                    .create();

            assertThat(result.getArrayString()).hasSize(length).doesNotContainNull();
            assertThat(result.getArrayStringHolder()).hasSize(length)
                    .allSatisfy(e -> assertThat(e.getValue()).isNull());
        }

        @Test
        void overrideCollectionSize() {
            final int size = 5;
            final Pojo result = Instancio.ofBlank(Pojo.class)
                    .generate(field(Pojo::getListString), gen -> gen.collection().size(size))
                    .generate(field(Pojo::getListStringHolder), gen -> gen.collection().size(size))
                    .create();

            assertThat(result.getListString()).hasSize(size).doesNotContainNull();
            assertThat(result.getListStringHolder()).hasSize(size)
                    .allSatisfy(e -> assertThat(e.getValue()).isNull());
        }

        @Test
        void overrideMapSize() {
            final int size = 1;
            final Pojo result = Instancio.ofBlank(Pojo.class)
                    .generate(field(Pojo::getMapStringHolderLong), gen -> gen.map().size(size))
                    .generate(field(Pojo::getMapLongStringHolder), gen -> gen.map().size(size))
                    .create();

            assertThat(result.getMapStringHolderLong()).hasSize(size);
            assertThat(result.getMapStringHolderLong().keySet()).doesNotContainNull()
                    .allSatisfy(stringHolder -> assertThat(stringHolder.getValue()).isNull());
            assertThat(result.getMapStringHolderLong().values()).doesNotContainNull();

            assertThat(result.getMapLongStringHolder()).hasSize(size);
            assertThat(result.getMapLongStringHolder().keySet()).doesNotContainNull();
            assertThat(result.getMapLongStringHolder().values()).doesNotContainNull()
                    .allSatisfy(stringHolder -> assertThat(stringHolder.getValue()).isNull());
        }

        @Test
        void mapWithSizeGreaterThanOne() {
            final int size = 2;
            final InstancioApi<Pojo> api = Instancio.ofBlank(Pojo.class)
                    .generate(field(Pojo::getMapStringHolderLong), gen -> gen.map().size(size));

            // Since the key is always a blank POJO, we can't generate distinct keys
            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(InstancioException.class)
                    .hasMessageContaining("unable to populate Map of size %d", size);
        }

        @Test
        void overrideOptionalString() {
            final Pojo result = Instancio.ofBlank(Pojo.class)
                    .generate(allStrings().within(scope(Pojo::getOptionalString)), Generators::string)
                    .create();

            assertThat(result.getOptionalString()).isNotEmpty();
            assertThat(result.getOptionalString().get()).isNotBlank();
        }


        @Test
        void rootObjectIsAPojo() {
            class Container {
                @Nullable List<OuterClass> list;
            }

            final Container result = Instancio.ofBlank(Container.class)
                    .generate(field("list"), gen -> gen.collection().size(1))
                    .create();

            assertThat(result.list).hasSize(1).allSatisfy((OuterClass outer) -> {
                assertThat(outer.getInnerClass().getValue()).isNull();
                assertThat(outer.getInnerStaticClass().getValue()).isNull();
                assertThat(outer.getInnermostStaticClass().getValue()).isNull();
            });
        }
    }

    @Getter
    @Setter
    @SuppressWarnings({"NullAway", "OptionalUsedAsFieldOrParameterType"})
    private static final class Pojo {
        private String string;
        private int primitive;
        private Optional<StringHolder> optionalStringHolder;
        private Optional<String> optionalString;
        private List<String> listString;
        private List<StringHolder> listStringHolder;
        private Map<Long, StringHolder> mapLongStringHolder; // key is a leaf
        private Map<StringHolder, Long> mapStringHolderLong; // key is a POJO
        private String[] arrayString;
        private StringHolder[] arrayStringHolder;
        private StringsAbc stringsAbc;

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
        }
    }
}
