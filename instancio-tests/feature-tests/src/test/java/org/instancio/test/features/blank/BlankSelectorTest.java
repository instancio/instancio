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

import org.instancio.Instancio;
import org.instancio.SelectorGroup;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.pojo.collections.lists.TwoListsOfItemString;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojo;
import org.instancio.test.support.pojo.cyclic.onetomany.MainPojoContainer;
import org.instancio.test.support.pojo.generics.basic.Item;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.Select.field;
import static org.instancio.Select.root;
import static org.instancio.Select.scope;
import static org.instancio.Select.types;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.BLANK, Feature.SELECTOR})
@ExtendWith(InstancioExtension.class)
class BlankSelectorTest {

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.FAIL_ON_ERROR, true);

    @Test
    void rootSelector() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .setBlank(root())
                .create();

        assertThat(result.getValue()).isNull();
    }

    @Test
    void classSelector_nestedPojo() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setBlank(all(StringsDef.class))
                .create();

        assertThat(result.a).isNotBlank();
        assertThat(result.b).isNotBlank();
        assertThat(result.c).isNotBlank();
        assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.def.ghi).hasAllFieldsOfTypeSetToNull(String.class);
    }

    @Test
    void classSelector_collectionElementPojo() {
        final Person result = Instancio.of(Person.class)
                .setBlank(all(Phone.class))
                .create();

        // blank elements
        assertThat(result.getAddress().getPhoneNumbers()).isNotEmpty()
                .allSatisfy(phone -> assertThatObject(phone).hasAllFieldsOfTypeSetToNull(String.class));

        // other fields should be populated
        assertThat(result.getName()).isNotBlank();
        assertThat(result.getAddress().getCity()).isNotBlank();
        assertThat(result.getPets()).isNotEmpty().allSatisfy(pet -> assertThat(pet.getName()).isNotBlank());
    }

    @Test
    void classSelector_withScope() {
        final TwoListsOfItemString result = Instancio.of(TwoListsOfItemString.class)
                .setBlank(all(Item.class).within(scope(TwoListsOfItemString::getList2)))
                .create();

        assertThat(result.getList1()).isNotEmpty().allSatisfy(item -> assertThat(item.getValue()).isNotBlank());
        assertThat(result.getList2()).isNotEmpty().allSatisfy(item -> assertThat(item.getValue()).isNull());
    }

    @Test
    void classSelector_withDepth() {
        final StringsAbc result = Instancio.of(StringsAbc.class)
                .setBlank(types().atDepth(d -> d > 1))
                .create();

        assertThat(result.a).isNotBlank();
        assertThat(result.b).isNotBlank();
        assertThat(result.c).isNotBlank();
        assertThatObject(result.def).hasAllFieldsOfTypeSetToNull(String.class);
        assertThatObject(result.def.ghi).hasAllFieldsOfTypeSetToNull(String.class);
    }

    @Test
    void selectorGroup() {
        final SelectorGroup group = all(
                field(MainPojoContainer::getMainPojo1),
                field(MainPojo::getDetailPojos).within(scope(MainPojoContainer::getMainPojo2)));

        final MainPojoContainer result = Instancio.of(MainPojoContainer.class)
                .setBlank(group)
                .create();

        assertThat(result.getMainPojo1().getId()).isNull();
        assertThat(result.getMainPojo2().getId()).isNotNull();

        assertThat(result.getMainPojo1().getDetailPojos()).isEmpty();
        assertThat(result.getMainPojo2().getDetailPojos()).isEmpty();
    }

    /**
     * Blank selectors are lenient and should not trigger the unused selector error.
     */
    @Test
    void unmatchedBlankSelector_shouldBeSilentlyIgnored() {
        final StringHolder result = Instancio.of(StringHolder.class)
                .setBlank(allInts())
                .create();

        assertThat(result.getValue()).isNotBlank();
    }
}
