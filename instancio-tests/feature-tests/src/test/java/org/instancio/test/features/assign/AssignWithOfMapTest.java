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
package org.instancio.test.features.assign;

import org.instancio.Assign;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Assign.given;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.test.support.asserts.ReflectionAssert.assertThatObject;

@FeatureTag({Feature.ASSIGN, Feature.OF_MAP})
@ExtendWith(InstancioExtension.class)
class AssignWithOfMapTest {

    @Test
    void valueConditional() {
        final Map<UUID, StringsGhi> results = Instancio.ofMap(UUID.class, StringsGhi.class)
                .generate(field(StringsGhi::getG), gen -> gen.oneOf("G1", "G2"))
                .assign(Assign.given(StringsGhi::getG).is("G2").set(field(StringsGhi::getI), "I2"))
                .create();

        assertThat(results.values()).isNotEmpty().allSatisfy(result -> {
            assertThat(result.getG()).isIn("G1", "G2");

            if ("G2".equals(result.getG())) {
                assertThat(result.getI()).isEqualTo("I2");
            } else {
                assertThat(result.getI()).as("expected random value").isNotEqualTo("I2");
            }
        });
    }

    @Test
    @DisplayName("Origin is map key, destination is map value")
    void keyValueConditional() {
        final Map<Integer, StringsGhi> results = Instancio.ofMap(Integer.class, StringsGhi.class)
                .assign(given(Integer.class).satisfies((Integer i) -> i % 2 == 0).set(allStrings(), "even"))
                .assign(given(Integer.class).satisfies((Integer i) -> i % 2 != 0).set(allStrings(), "odd"))
                .create();

        assertThat(results).isNotEmpty().allSatisfy((key, value) -> {
            if (key % 2 == 0) {
                assertThatObject(value).hasAllFieldsOfTypeEqualTo(String.class, "even");
            } else {
                assertThatObject(value).hasAllFieldsOfTypeEqualTo(String.class, "odd");
            }
        });
    }
}
