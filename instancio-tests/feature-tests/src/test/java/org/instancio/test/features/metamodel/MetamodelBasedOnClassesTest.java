/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio.test.features.metamodel;

import org.instancio.Instancio;
import org.instancio.InstancioMetamodel;
import org.instancio.TypeToken;
import org.instancio.internal.selectors.PrimitiveAndWrapperSelectorImpl;
import org.instancio.internal.selectors.SelectorImpl;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.generics.MiscFields;
import org.instancio.test.support.pojo.generics.MiscFields_;
import org.instancio.test.support.pojo.nested.InnerClass_;
import org.instancio.test.support.pojo.nested.InnerStaticClass_;
import org.instancio.test.support.pojo.nested.InnermostStaticClass_;
import org.instancio.test.support.pojo.nested.OuterClass;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Address_;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.pojo.person.Phone;
import org.instancio.test.support.pojo.person.Phone_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.util.ReflectionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.reflect.Field;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allInts;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.scope;

@InstancioMetamodel(classes = {
        Address.class,
        Phone.class,
        MiscFields.class,
        Person.class,
        SupportedNumericTypes.class,
        OuterClass.class,
        OuterClass.InnerClass.class,
        OuterClass.InnerStaticClass.class,
        OuterClass.InnerStaticClass.InnermostStaticClass.class
})
@FeatureTag(Feature.METAMODEL)
@ExtendWith(InstancioExtension.class)
class MetamodelBasedOnClassesTest {

    @WithSettings
    private final Settings settings = Settings.create().set(Keys.STRING_MIN_LENGTH, 15);

    @Test
    void nestedClasses() {
        final String expectedInner = "foo";
        final String expectedInnerStatic = "bar";
        final String expectedInnermostStatic = "baz";

        final OuterClass result = Instancio.of(OuterClass.class)
                .set(InnerClass_.value, expectedInner)
                .set(InnerStaticClass_.value, expectedInnerStatic)
                .set(InnermostStaticClass_.value, expectedInnermostStatic)
                .create();

        assertThat(result.getInnerClass().getValue()).isEqualTo(expectedInner);
        assertThat(result.getInnerStaticClass().getValue()).isEqualTo(expectedInnerStatic);
        assertThat(result.getInnermostStaticClass().getValue()).isEqualTo(expectedInnermostStatic);
    }

    @Test
    void person() {
        final int expected = 20;
        final Person result = Instancio.of(Person.class)
                .generate(Person_.age, gen -> gen.ints().range(expected, expected))
                .create();

        assertThat(result.getAge()).isEqualTo(expected);
    }

    @Test
    void miscFields() {
        final String expectedValue = Instancio.create(String.class);
        final MiscFields<String, Long, UUID> result = Instancio.of(new TypeToken<MiscFields<String, Long, UUID>>() {})
                .set(MiscFields_.fieldA, expectedValue)
                .create();

        assertThat(result.getFieldA()).isEqualTo(expectedValue);
    }

    @Test
    void metamodelDoesNotIncludeStaticFields() {
        final Field staticField = ReflectionUtils.getField(Person.class, "staticField");
        final Field staticFinalField = ReflectionUtils.getField(Person.class, "STATIC_FINAL_FIELD");

        assertThatThrownBy(() -> ReflectionUtils.getField(Person_.class, staticField.getName()))
                .hasMessageContaining("Invalid field");

        assertThatThrownBy(() -> ReflectionUtils.getField(Person_.class, staticFinalField.getName()))
                .hasMessageContaining("Invalid field");
    }

    @Test
    void withinScope() {
        final String foo = "foo";
        final String bar = "bar";
        final int baz = 1;
        final Person result = Instancio.of(Person.class)
                .set(Phone_.number.within(Address_.phoneNumbers.toScope()), foo)
                .set(allStrings().within(Person_.address.toScope()), bar)
                .set(allInts().within(scope(Person.class)), baz)
                .create();

        // Calling within() on a metamodel field should not modify it, but return a new selector with scope
        assertThat(((SelectorImpl) Phone_.number).getScopes()).isEmpty();
        assertThat(((SelectorImpl) allStrings()).getScopes()).isEmpty();
        assertThat(((PrimitiveAndWrapperSelectorImpl) allInts()).flatten()).allSatisfy(selector ->
                assertThat(((SelectorImpl) selector).getScopes()).isEmpty());

        assertThat(result.getAddress().getPhoneNumbers()).extracting(Phone::getNumber).containsOnly(foo);
        assertThat(result.getAddress().getPhoneNumbers()).extracting(Phone::getCountryCode).containsOnly(bar);
        assertThat(result.getAddress().getCity()).isEqualTo(bar);
        assertThat(result.getAddress().getCountry()).isEqualTo(bar);
        assertThat(result.getName()).isNotEqualTo(bar);
        assertThat(result.getAge()).isEqualTo(baz);
    }
}
