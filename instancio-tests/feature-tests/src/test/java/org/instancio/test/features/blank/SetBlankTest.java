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
package org.instancio.test.features.blank;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.TargetSelector;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfLong;
import org.instancio.test.support.pojo.misc.StringFields;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

/**
 * Tests for {@link InstancioApi#setBlank(TargetSelector)}.
 */
@FeatureTag(Feature.BLANK)
@ExtendWith(InstancioExtension.class)
class SetBlankTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void setBlankField_valueType() {
        final StringFields result = Instancio.of(StringFields.class)
                .setBlank(field(StringFields::getOne))
                .setBlank(field(StringFields::getThree))
                .create();

        assertThat(result).hasNoNullFieldsOrPropertiesExcept("one", "three");
    }

    @Test
    void setBlankField_collectionOfValueType() {
        final TwoListsOfLong result = Instancio.of(TwoListsOfLong.class)
                .setBlank(field(TwoListsOfLong::getList1))
                .create();

        assertThat(result.getList1()).isEmpty();
        assertThat(result.getList2()).isNotEmpty().doesNotContainNull();
    }

    @Test
    void setBlankFieldCollectionOfPojo() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .setBlank(field(TwoListsOfItemString::getList1))
                .create();

        assertThat(result.getList1()).isEmpty();
        assertThat(result.getList2()).isNotEmpty()
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }

    @Test
    void setBlankFieldCollectionOfPojoAndCollectionOfSizeOverride() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .setBlank(field(TwoListsOfItemString::getList1))
                .generate(field(TwoListsOfItemString::getList1), gen -> gen.collection().size(2))
                .create();

        assertThat(result.getList1()).hasSize(2)
                .allSatisfy(item -> assertThat(item.getValue()).isNull());

        assertThat(result.getList2()).isNotEmpty()
                .allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
    }

    @Test
    void setBlankNestedPojo() {
        @Getter
        class Container {
            // This should be blank
            Pojo nestedPojo;
            // These should be populated
            List<StringHolder> listStringHolder;
            Map<StringHolder, Long> mapStringHolderLong;
            StringHolder[] arrayStringHolder;
        }

        final Container result = Instancio.of(Container.class)
                .setBlank(field(Container::getNestedPojo))
                .create();

        assertBlankPojo(result.nestedPojo);
        assertThatObject(result.listStringHolder).isFullyPopulated();
        assertThatObject(result.mapStringHolderLong).isFullyPopulated();
        assertThatObject(result.arrayStringHolder).isFullyPopulated();
    }

    private static void assertBlankPojo(final Pojo result) {
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

    @Getter
    @Setter
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
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
