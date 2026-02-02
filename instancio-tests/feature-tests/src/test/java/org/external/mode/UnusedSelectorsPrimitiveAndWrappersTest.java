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
package org.external.mode;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.internal.util.Sonar;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.instancio.Select.all;
import static org.instancio.Select.allInts;
import static org.instancio.test.support.UnusedSelectorsAssert.assertThrowsUnusedSelectorException;

@FeatureTag(Feature.MODE)
@ExtendWith(InstancioExtension.class)
class UnusedSelectorsPrimitiveAndWrappersTest {

    @Test
    @DisplayName("Error selecting Integer when only primitive int is present")
    void allIntegersWithClassContainingOnlyPrimitiveUsed() {
        final InstancioApi<IntHolder> api = Instancio.of(IntHolder.class)
                .ignore(all(Integer.class));

        assertThrowsUnusedSelectorException(api)
                .hasUnusedSelectorCount(1)
                .ignoreSelector(all(Integer.class),
                        "UnusedSelectorsPrimitiveAndWrappersTest.java:40");
    }

    @Test
    @DisplayName("No error with allInts() even though class has no Integer field")
    @SuppressWarnings(Sonar.ADD_ASSERTION)
    void allIntsWithClassContainingOnlyPrimitiveUsed() {
        Instancio.of(IntHolder.class).ignore(allInts()).create();
        Instancio.of(IntHolder.class).ignore(all(int.class)).create();
    }

    private static class IntHolder {
        @SuppressWarnings("FieldCanBeLocal")
        private int value;

        public void setValue(final int value) {
            this.value = value;
        }
    }
}
