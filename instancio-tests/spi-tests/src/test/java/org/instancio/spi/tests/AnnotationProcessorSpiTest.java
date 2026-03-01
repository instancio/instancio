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
package org.instancio.spi.tests;

import org.example.spi.CustomAnnotationProcessor;
import org.example.spi.CustomAnnotationProcessor.AnnotationWithTwoAnnotationHandlerMethods;
import org.example.spi.CustomAnnotationProcessor.CustomLongMax;
import org.example.spi.CustomAnnotationProcessor.CustomLongMin;
import org.example.spi.CustomAnnotationProcessor.EmptyString;
import org.example.spi.CustomAnnotationProcessor.WithKeys;
import org.example.spi.CustomGeneratorProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Tests for annotation processor SPI.
 *
 * @see CustomAnnotationProcessor
 */
@ExtendWith(InstancioExtension.class)
class AnnotationProcessorSpiTest {

    @Test
    void customAnnotation() {
        class Pojo {
            @Nullable
            @WithKeys({"foo", "bar"})
            Map<CharSequence, Long> map;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.map).containsKeys("foo", "bar");
    }

    @Test
    void generatorProviderTakesPrecedenceOverAnnotationProcessor() {
        class Pojo {
            @Nullable
            // since GeneratorProvider takes precedence, this annotation is ignored
            @EmptyString
            String value;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.value).isEqualTo(CustomGeneratorProvider.STRING_GENERATOR_VALUE);
    }

    @Test
    void multipleAnnotations() {
        class Pojo {
            @CustomLongMin(-1)
            @CustomLongMax(-1)
            long value;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.value).isEqualTo(-1);
    }

    @Test
    void annotationWithTwoAnnotationHandlerMethods() {
        class Pojo {
            @AnnotationWithTwoAnnotationHandlerMethods
            long value;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(CustomAnnotationProcessor.getDuplicateHandlerInvocationCount()).isEqualTo(2);
        assertThat(result.value).isEqualTo(2);
    }

    @Test
    void shouldBeAbleToOverrideAnnotationsViaApi() {
        class Pojo {
            @CustomLongMin(-1)
            @CustomLongMax(-1)
            long value;
        }

        final Pojo result = Instancio.of(Pojo.class)
                .set(field("value"), -2)
                .create();

        assertThat(result.value).isEqualTo(-2);
    }
}
