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
package org.instancio.test.features.setmodel;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.Model;
import org.instancio.exception.UnusedSelectorException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.all;
import static org.instancio.Select.allInts;

/**
 * Verify the following cases:
 *
 * <ul>
 *   <li>strict model - strict selector</li>
 *   <li>strict model - lenient selector</li>
 *   <li>lenient model - strict selector</li>
 *   <li>lenient model - lenient selector</li>
 *   <li>lenient model - unused selector in Instancio.create()</li>
 * </ul>
 */
@FeatureTag({
        Feature.MODEL,
        Feature.SET_MODEL,
        Feature.LENIENT_SELECTOR,
        Feature.MODE
})
@ExtendWith(InstancioExtension.class)
class SetModelLenientTest {

    @Nested
    class StrictModelTest {
        @Test
        void strictModel_strictSelector() {
            final Model<StringHolder> strictModel = Instancio.of(StringHolder.class)
                    .set(allInts(), -1) // strict selector (unused because StringHolder has no ints)
                    .toModel();

            final InstancioApi<List<StringHolder>> api = Instancio.ofList(StringHolder.class)
                    .setModel(all(StringHolder.class), strictModel);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContainingAll("Unused selectors", "allInts()");
        }

        /**
         * Should not throw unused selector error because
         * the model's selector itself is lenient.
         */
        @Test
        void strictModel_lenientSelector() {
            final Model<StringHolder> strictModel = Instancio.of(StringHolder.class)
                    .set(allInts().lenient(), -1) // lenient selector
                    .toModel();

            final List<StringHolder> results = Instancio.ofList(StringHolder.class)
                    .setModel(all(StringHolder.class), strictModel)
                    .create();

            assertThat(results).isNotEmpty().allSatisfy(r -> assertThat(r.getValue()).isNotBlank());
        }
    }

    @Nested
    class LenientModelTest {
        /**
         * Passing a lenient model via setModel() should
         * not make the recipient model itself lenient.
         */
        @Test
        void lenientModel_strictSelector() {
            final Model<StringHolder> lenientModel = Instancio.of(StringHolder.class)
                    .set(allInts(), -1) // strict selector
                    .lenient()
                    .toModel();

            final InstancioApi<List<StringHolder>> api = Instancio.ofList(StringHolder.class)
                    .setModel(all(StringHolder.class), lenientModel);

            assertThatThrownBy(api::create)
                    .isExactlyInstanceOf(UnusedSelectorException.class)
                    .hasMessageContainingAll("Unused selectors", "allInts()");
        }

        @Test
        void lenientModel_lenientSelector() {
            final Model<StringHolder> lenientModel = Instancio.of(StringHolder.class)
                    .set(allInts().lenient(), -1) // lenient selector
                    .lenient()
                    .toModel();

            final List<StringHolder> result = Instancio.ofList(StringHolder.class)
                    .setModel(all(StringHolder.class), lenientModel)
                    .create();

            assertThat(result).as("Should not throw unused selector error").isNotEmpty();
        }
    }
}