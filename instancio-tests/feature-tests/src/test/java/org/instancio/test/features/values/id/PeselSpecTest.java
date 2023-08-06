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
package org.instancio.test.features.values.id;

import org.instancio.Gen;
import org.instancio.generator.specs.PeselSpec;
import org.instancio.test.features.values.AbstractValueSpecTestTemplate;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.internal.util.NumberUtils.toDigitInt;
import static org.instancio.test.support.conditions.Conditions.EVEN_NUMBER;
import static org.instancio.test.support.conditions.Conditions.ODD_NUMBER;

@FeatureTag(Feature.VALUE_SPEC)
class PeselSpecTest extends AbstractValueSpecTestTemplate<String> {

    @Override
    protected PeselSpec spec() {
        return Gen.id().pesel();
    }

    @Override
    protected void assertDefaultSpecValue(final String actual) {
        assertThat(actual)
                .containsOnlyDigits()
                .hasSize(11);
    }

    @Test
    void birthdate() {
        final LocalDate localDate = LocalDate.of(1990, 1, 1);
        assertThat(spec().birthdate(random -> localDate).get())
                .containsOnlyDigits()
                .startsWith("900101");
    }

    @Test
    void male() {
        int maleDigit = toDigitInt(spec().male().get().charAt(9));
        assertThat(maleDigit).is(ODD_NUMBER);
    }

    @Test
    void female() {
        int femaleDigit = toDigitInt(spec().female().get().charAt(9));
        assertThat(femaleDigit).is(EVEN_NUMBER);
    }
}
