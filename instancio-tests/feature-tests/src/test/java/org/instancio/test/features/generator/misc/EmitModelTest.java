/*
 * Copyright 2022-2023 the original author or authors.
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
package org.instancio.test.features.generator.misc;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.EMIT_GENERATOR, Feature.MODEL})
@ExtendWith(InstancioExtension.class)
class EmitModelTest {

    @Test
    void emitWithTwoModels() {
        final String expected = "foo";

        final Model<String> model = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.emit().items(expected))
                .toModel();

        // Each root object should have its own copy of items to emit
        // even though they share the same instance of a model
        final String result1 = Instancio.create(model);
        final String result2 = Instancio.create(model);

        assertThat(result1).isEqualTo(result2).isEqualTo(expected);
    }

}
