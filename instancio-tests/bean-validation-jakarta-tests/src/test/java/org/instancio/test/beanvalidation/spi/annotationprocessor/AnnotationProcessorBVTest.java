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
package org.instancio.test.beanvalidation.spi.annotationprocessor;

import jakarta.validation.constraints.Size;
import org.annotations.StringSuffix;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class AnnotationProcessorBVTest {

    private static final int SIZE = 10;
    private static final String FOO = "_foo";
    private static final String BARBAZ = "_barbaz";

    @WithSettings
    private static final Settings SETTINGS = Settings.create()
            .set(Keys.COLLECTION_NULLABLE, false)
            .set(Keys.STRING_NULLABLE, false);

    private static class Pojo {
        @StringSuffix(FOO)
        @Size(min = SIZE, max = SIZE)
        String value;

        List<@Size(min = SIZE, max = SIZE) @StringSuffix(BARBAZ) String> list;
    }

    @RepeatedTest(Constants.SAMPLE_SIZE_D)
    void beanValidationWithCustomAnnotation() {
        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.value)
                .hasSize(SIZE + FOO.length())
                .endsWith(FOO);

        assertThat(result.list).allSatisfy(s -> assertThat(s)
                .hasSize(SIZE + BARBAZ.length())
                .endsWith(BARBAZ));
    }
}
