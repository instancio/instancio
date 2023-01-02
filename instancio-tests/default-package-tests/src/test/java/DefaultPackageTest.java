/*
 *  Copyright 2022-2023 the original author or authors.
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

import org.instancio.Instancio;
import org.instancio.InstancioMetamodel;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@FeatureTag(Feature.METAMODEL)
@InstancioMetamodel(classes = DefaultPackageClass.class)
class DefaultPackageTest {

    @Test
    void create() {
        final DefaultPackageClass result = Instancio.create(DefaultPackageClass.class);
        assertThat(result.getValue()).isNotBlank();
    }

    @Test
    void withMetamodel() {
        final String expected = "some value";
        final DefaultPackageClass result = Instancio.of(DefaultPackageClass.class)
                .supply(DefaultPackageClass_.value, () -> expected)
                .create();

        assertThat(result.getValue()).isEqualTo(expected);
    }
}
