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
package org.instancio.test.features.withunique;

import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;

@FeatureTag({Feature.WITH_UNIQUE, Feature.CARTESIAN_PRODUCT})
@ExtendWith(InstancioExtension.class)
class WithUniqueCartesianProductTest {

    @Test
    void cartesianProduct() {
        @Data
        class Pojo {
            String a, b, c;
        }

        final List<Pojo> result = Instancio.ofCartesianProduct(Pojo.class)
                .with(field(Pojo::getA), "a1", "a2")
                .with(field(Pojo::getB), "b")
                .generate(allStrings(), gen -> gen.oneOf("foo", "bar"))
                .withUnique(field(Pojo::getC))
                .create();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Pojo::getA).containsExactly("a1", "a2");
        assertThat(result).extracting(Pojo::getB).containsExactly("b", "b");
        assertThat(result).extracting(Pojo::getC).containsExactlyInAnyOrder("foo", "bar");
    }
}
