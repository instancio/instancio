/*
 * Copyright 2022 the original author or authors.
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

package org.instancio.test.features.generator.string;

import org.instancio.Instancio;
import org.instancio.generator.specs.StringGeneratorSpec;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.pojo.basic.StringHolder;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag({Feature.GENERATE, Feature.STRING_GENERATOR})
@ExtendWith(InstancioExtension.class)
class StringGeneratorTest {

    @Test
    void stringType() {
        assertStringType(StringGeneratorSpec::lowerCase, "^[a-z]+$");
        assertStringType(StringGeneratorSpec::upperCase, "^[A-Z]+$");
        assertStringType(StringGeneratorSpec::mixedCase, "^[a-zA-Z]+$");
        assertStringType(StringGeneratorSpec::digits, "^[0-9]+$");
        assertStringType(StringGeneratorSpec::alphaNumeric, "^[a-zA-Z0-9]+$");
    }

    private void assertStringType(Function<StringGeneratorSpec, StringGeneratorSpec> fn, String expectedPattern) {
        final StringHolder result = Instancio.of(StringHolder.class)
                .generate(allStrings(), gen -> fn.apply(gen.string()))
                .create();

        assertThat(result.getValue()).matches(expectedPattern);
    }
}