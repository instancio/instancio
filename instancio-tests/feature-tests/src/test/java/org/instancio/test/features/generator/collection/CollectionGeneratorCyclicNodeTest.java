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
package org.instancio.test.features.generator.collection;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

@FeatureTag(Feature.CYCLIC)
@ExtendWith(InstancioExtension.class)
class CollectionGeneratorCyclicNodeTest {

    //@formatter:off
    private static @Data class Root { A a; }
    private static @Data class A { B b; }
    private static @Data class B { List<A> list; }
    //@formatter:on

    @Test
    void create() {
        final Root result = Instancio.create(Root.class);

        assertThat(result.a.b.list).isEmpty();
    }

    /**
     * No exception should be thrown when {@link Keys#FAIL_ON_ERROR} is enabled.
     */
    @Test
    void withElement() {
        final A expectedElement = new A();

        final Root result = Instancio.of(Root.class)
                .withSetting(Keys.FAIL_ON_ERROR, true)
                .generate(field(B::getList), gen -> gen.collection()
                        .size(100) // won't be able to generate but should not throw an error
                        .with(expectedElement))
                .create();

        assertThat(result.a.b.list).containsOnly(expectedElement);
    }
}