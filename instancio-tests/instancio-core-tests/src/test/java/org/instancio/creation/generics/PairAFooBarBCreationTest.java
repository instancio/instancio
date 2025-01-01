/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.test.support.pojo.generics.basic.Pair;
import org.instancio.test.support.pojo.generics.foobarbaz.Bar;
import org.instancio.test.support.pojo.generics.foobarbaz.Foo;
import org.instancio.test.support.pojo.generics.foobarbaz.PairAFooBarB;
import org.instancio.test.support.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class PairAFooBarBCreationTest extends CreationTestTemplate<PairAFooBarB<UUID, String>> {

    @Override
    protected void verify(PairAFooBarB<UUID, String> result) {
        // Pair<A, Foo<Bar<B>>> pairAFooBarB

        assertThat(result.getPairAFooBarB()).isInstanceOf(Pair.class);
        assertThat(result.getPairAFooBarB().getLeft()).isInstanceOf(UUID.class);
        assertThat(result.getPairAFooBarB().getRight()).isInstanceOf(Foo.class)
                .satisfies(foo -> {
                    assertThat(foo.getOtherFooValue()).isExactlyInstanceOf(Object.class);
                    assertThat(foo.getFooValue()).isInstanceOf(Bar.class)
                            .satisfies(bar -> {
                                assertThat(bar.getOtherBarValue()).isExactlyInstanceOf(Object.class);
                                assertThat(bar.getBarValue()).isInstanceOf(String.class);
                            });
                });
    }

}
