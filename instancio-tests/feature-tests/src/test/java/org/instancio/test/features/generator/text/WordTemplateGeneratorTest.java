/*
 * Copyright 2022-2025 the original author or authors.
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

package org.instancio.test.features.generator.text;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.allStrings;

/**
 * Note: some adverbs might contain a hyphen, e.g. willy-nilly.
 */
@FeatureTag({Feature.GENERATE, Feature.WORD_TEMPLATE_GENERATOR})
@ExtendWith(InstancioExtension.class)
class WordTemplateGeneratorTest {

    @ValueSource(strings = {
            "${adjective} ${adverb} ${noun} ${verb}",
            "${aDJECTIVE} ${adveRB} ${nOUn} ${vErB}"
    })
    @ParameterizedTest
    void lowercase(final String template) {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().wordTemplate(template))
                .create();

        assertThat(result).matches(
                "\\p{Lower}+ \\p{Lower}+(-\\p{Lower}+)? \\p{Lower}+ \\p{Lower}+");
    }

    @ValueSource(strings = {
            "${ADJECTIVE} ${ADVERB} ${NOUN} ${VERB}",
            "${AdjectivE} ${AdVErB} ${NouN} ${VErB}"
    })
    @ParameterizedTest
    void uppercase(final String template) {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().wordTemplate(template))
                .create();

        assertThat(result).matches(
                "\\p{Upper}+ \\p{Upper}+(-\\p{Upper}+)? \\p{Upper}+ \\p{Upper}+");
    }

    @Test
    void capitalised() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().wordTemplate(
                        "${Adjective} ${Adverb} ${Noun} ${Verb}"))
                .create();

        assertThat(result).matches(
                "\\p{Upper}\\p{Lower}* \\p{Upper}\\p{Lower}*(-\\p{Lower}+)? \\p{Upper}\\p{Lower}* \\p{Upper}\\p{Lower}*");
    }

    @Test
    void wordsWithWhitespace() {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text()
                        .wordTemplate(" \t - ${Adjective} - ${NOUN} - ${noun} - ${VERB} - \n "))
                .create();

        assertThat(result).matches(
                " \t - \\p{Upper}\\p{Lower}* - \\p{Upper}+ - \\p{Lower}+ - \\p{Upper}+ - \n ");
    }

    @ValueSource(strings = {"", " ", "foo", "$", "{", "}", "$foo", "{foo}"})
    @ParameterizedTest
    void templateWithoutKeys(final String template) {
        final String result = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().wordTemplate(template))
                .create();

        assertThat(result).isEqualTo(template);
    }

    @Test
    void invalidTemplate() {
        final InstancioApi<String> api = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().wordTemplate("bad template ${noun ..."));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Reason: the specified template is invalid")
                .hasMessageContaining(" -> \"bad template ${noun ...\"")
                .hasMessageContaining("Cause: unterminated template key");
    }

    @Test
    void invalidTemplateKey() {
        final InstancioApi<String> api = Instancio.of(String.class)
                .generate(allStrings(), gen -> gen.text().wordTemplate("bad template key ${foo} ..."));

        assertThatThrownBy(api::create)
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContaining("Reason: the specified template is invalid")
                .hasMessageContaining("-> \"bad template key ${foo} ...\"")
                .hasMessageContaining("Cause: invalid template key '${foo}'");
    }
}