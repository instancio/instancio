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
package org.instancio.test.features.conditional;

import org.instancio.Instancio;
import org.instancio.Select;
import org.instancio.Selector;
import org.instancio.SelectorGroup;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.misc.StringsAbc;
import org.instancio.test.support.pojo.misc.StringsDef;
import org.instancio.test.support.pojo.misc.StringsGhi;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;
import static org.instancio.Select.field;
import static org.instancio.Select.valueOf;

@FeatureTag({Feature.CONDITIONAL, Feature.SELECTOR})
@ExtendWith(InstancioExtension.class)
class ConditionalWithSelectorGroupTest {

    private static final String EXPECTED = "foo";

    @Test
    @DisplayName("Should set all destination selectors to the expected value")
    void destinationGroup() {
        final SelectorGroup group = Select.all(
                field(StringsAbc::getA),
                field(StringsAbc::getB),
                field(StringsDef::getD),
                field(StringsDef::getF),
                field(StringsGhi::getI));

        final StringsAbc result = Instancio.of(StringsAbc.class)
                .when(valueOf(StringsAbc::getC).satisfies(c -> true).set(group, EXPECTED))
                .create();

        assertThat(result.a).isEqualTo(EXPECTED);
        assertThat(result.b).isEqualTo(EXPECTED);
        assertThat(result.def.d).isEqualTo(EXPECTED);
        assertThat(result.def.f).isEqualTo(EXPECTED);
        assertThat(result.def.ghi.i).isEqualTo(EXPECTED);

        // remaining
        assertThat(result.c).isNotEqualTo(EXPECTED);
        assertThat(result.def.e).isNotEqualTo(EXPECTED);
        assertThat(result.def.ghi.g).isNotEqualTo(EXPECTED);
        assertThat(result.def.ghi.h).isNotEqualTo(EXPECTED);
    }

    @Test
    void originDoesNotAllowSelectorGroup() {
        final SelectorGroup selector = Select.all(allStrings());

        assertThatThrownBy(() -> valueOf(selector))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("valueOf() does not support selector groups");
    }

    @Test
    void originDoesNotAllowPrimitiveAndWrapperSelector() {
        final Selector selector = Select.allInts();

        assertThatThrownBy(() -> valueOf(selector))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("valueOf() does not support primitive and wrapper selectors such as allInts()");
    }
}
