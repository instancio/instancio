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
import org.instancio.InstancioMetaModel;
import org.instancio.TypeToken;
import org.instancio.test.support.pojo.basic.SupportedNumericTypes;
import org.instancio.test.support.pojo.generics.MiscFields;
import org.instancio.test.support.pojo.generics.MiscFields_;
import org.instancio.test.support.pojo.nested.InnerClass_;
import org.instancio.test.support.pojo.nested.InnerStaticClass_;
import org.instancio.test.support.pojo.nested.InnermostStaticClass_;
import org.instancio.test.support.pojo.nested.OuterClass;
import org.instancio.test.support.pojo.person.Address;
import org.instancio.test.support.pojo.person.Person;
import org.instancio.test.support.pojo.person.Person_;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@InstancioMetaModel(classes = {
        Address.class,
        MiscFields.class,
        Person.class,
        SupportedNumericTypes.class,
        OuterClass.class,
        OuterClass.InnerClass.class,
        OuterClass.InnerStaticClass.class,
        OuterClass.InnerStaticClass.InnermostStaticClass.class
})
@FeatureTag(Feature.METAMODEL)
class MetaModelTest {

    @Test
    void nestedClasses() {
        final String expectedInner = "foo";
        final String expectedInnerStatic = "bar";
        final String expectedInnermostStatic = "baz";

        final OuterClass result = Instancio.of(OuterClass.class)
                .supply(InnerClass_.value, () -> expectedInner)
                .supply(InnerStaticClass_.value, () -> expectedInnerStatic)
                .supply(InnermostStaticClass_.value, () -> expectedInnermostStatic)
                .create();

        assertThat(result.getInnerClass().getValue()).isEqualTo(expectedInner);
        assertThat(result.getInnerStaticClass().getValue()).isEqualTo(expectedInnerStatic);
        assertThat(result.getInnermostStaticClass().getValue()).isEqualTo(expectedInnermostStatic);
    }

    @Test
    void person() {
        final int expected = 20;
        final Person result = Instancio.of(Person.class)
                .generate(Person_.age, gen -> gen.ints().range(expected, expected + 1))
                .create();

        assertThat(result.getAge()).isEqualTo(expected);
    }

    @Test
    void miscFields() {
        final String expectedValue = Instancio.create(String.class);
        final MiscFields<String, Long, UUID> result = Instancio.of(new TypeToken<MiscFields<String, Long, UUID>>() {})
                .supply(MiscFields_.fieldA, () -> expectedValue)
                .create();

        assertThat(result.getFieldA()).isEqualTo(expectedValue);
    }
}
