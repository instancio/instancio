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
package org.instancio.test.features.selector;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.cyclic.ClassesABCWithCrossReferences;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.fields;
import static org.instancio.test.support.asserts.Asserts.assertAllNulls;

@FeatureTag({
        Feature.PREDICATE_SELECTOR,
        Feature.SELECTOR,
        Feature.DEPTH_SELECTOR,
        Feature.IGNORE
})
@ExtendWith(InstancioExtension.class)
class DepthSelectorWithIgnoreTest {

    @Test
    void classWithCrossReferences() {
        final ClassesABCWithCrossReferences result = Instancio.of(ClassesABCWithCrossReferences.class)
                .ignore(fields().matching("object.*").atDepth(d -> d > 1))
                .create();

        assertThat(result).hasNoNullFieldsOrProperties();
        assertAllNulls(
                // A
                result.getObjectA().getObjectA(),
                result.getObjectA().getObjectB(),
                result.getObjectA().getObjectC(),
                // B
                result.getObjectB().getObjectA(),
                result.getObjectB().getObjectB(),
                result.getObjectB().getObjectC(),
                // C
                result.getObjectC().getObjectA(),
                result.getObjectC().getObjectB(),
                result.getObjectC().getObjectC()
        );
    }
}
