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

import org.instancio.test.support.pojo.generics.TripletAFooBarBazStringListOfB;
import org.instancio.test.support.pojo.generics.basic.Triplet;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Baz;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.Constants;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class TripletAFooBarBazStringListOfBCreationTest extends CreationTestTemplate<TripletAFooBarBazStringListOfB<UUID, Long>> {

    @Override
    protected void verify(TripletAFooBarBazStringListOfB<UUID, Long> result) {
        final Triplet<?, Foo<Bar<Baz<String>>>, ? extends List<?>> triplet = result.getTripletA_FooBarBazString_ListOfB();
        assertThat(triplet).isInstanceOf(Triplet.class);

        assertThat(triplet.getLeft()).isInstanceOf(UUID.class);

        assertThat(triplet.getMid()).isInstanceOf(Foo.class).satisfies(foo -> {
            assertThat(foo.getOtherFooValue()).isExactlyInstanceOf(Object.class);
            assertThat(foo.getFooValue()).isInstanceOf(Bar.class)
                    .satisfies(bar -> {
                        assertThat(bar.getOtherBarValue()).isExactlyInstanceOf(Object.class);
                        assertThat(bar.getBarValue()).isInstanceOf(Baz.class)
                                .satisfies(baz -> assertThat(baz.getBazValue()).isInstanceOf(String.class).isNotBlank());
                    });
        });

        assertThat(triplet.getRight()).isInstanceOf(List.class)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .hasOnlyElementsOfType(Long.class);
    }
}
