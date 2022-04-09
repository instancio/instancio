/*
 * Copyright 2022 the original author or authors.
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
package org.instancio.creation.generics;

import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.generics.foobarbaz.FooBarBazContainer;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class FooBarBazContainerCreationTest extends CreationTestTemplate<FooBarBazContainer> {

    @Override
    protected void verify(FooBarBazContainer result) {
        final Foo<Bar<Baz<String>>> item = result.getItem();
        assertThat(item).isNotNull();
        assertThat(item.getOtherFooValue()).isExactlyInstanceOf(Object.class);

        final Bar<Baz<String>> fooValue = item.getFooValue();
        assertThat(fooValue).isExactlyInstanceOf(Bar.class);
        assertThat(fooValue.getOtherBarValue()).isExactlyInstanceOf(Object.class);

        final Baz<String> barValue = fooValue.getBarValue();
        assertThat(barValue).isExactlyInstanceOf(Baz.class);
        assertThat(barValue.getBazValue()).isInstanceOf(String.class);
    }
}
