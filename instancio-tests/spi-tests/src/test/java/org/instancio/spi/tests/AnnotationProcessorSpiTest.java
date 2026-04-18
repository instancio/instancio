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
import org.example.spi.CustomAnnotationProcessor.CustomNullable;
import org.example.spi.CustomAnnotationProcessor.DualTargetLongValue;
import org.example.spi.CustomAnnotationProcessor.EmptyString;
import org.example.spi.CustomAnnotationProcessor.TypeUseLongValue;
import org.example.spi.CustomAnnotationProcessor.WithKeys;
import org.example.spi.CustomGeneratorProvider;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.test.support.util.Constants;
import org.jspecify.annotations.NullUnmarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;

/**
 * Tests for annotation processor SPI.
 *
 * @see CustomAnnotationProcessor
 */
// JSpecify annotations are intentionally omitted: seeing @Nullable on a field
// might imply that nullable values should be produced via the SPI annotation
// processor, which is not what is being tested here, hence @NullUnmarked.
@NullUnmarked
@ExtendWith(InstancioExtension.class)
class AnnotationProcessorSpiTest {

    @Test
    void customAnnotation() {
        class Pojo {
            @WithKeys({"foo", "bar"})
            Map<CharSequence, Long> map;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.map).containsKeys("foo", "bar");
    }

    @Test
    void generatorProviderTakesPrecedenceOverAnnotationProcessor() {
        class Pojo {
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

    @Test
    void typeUseOnlyAnnotationOnClassField() {
        class Pojo {
            @TypeUseLongValue(42)
            Long value;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.value).isEqualTo(42L);
    }

    @Test
    void typeUseOnlyAnnotationOnRecordComponent() {
        record Rec(@TypeUseLongValue(7) Long value) {}

        final Rec result = Instancio.create(Rec.class);

        assertThat(result.value()).isEqualTo(7L);
    }

    @Test
    void typeUseOnlyAnnotationOnArrayComponent() {
        class Pojo {
            @TypeUseLongValue(99)
            Long[] arr;
        }

        final Pojo result = Instancio.create(Pojo.class);

        assertThat(result.arr)
                .isNotEmpty()
                .allSatisfy(v -> assertThat(v).isEqualTo(99L));
    }

    /**
     * NOTE: using {@code Long} instead of {@link String} because
     * {@link org.example.spi.CustomGeneratorProvider} overrides the String
     * generator in this module which bypasses annotation processing.
     */
    @Test
    void typeUseNullableAnnotationOnRecordComponent() {
        record Foo(@CustomNullable Long value) {}

        final Stream<Foo> result = Instancio.of(Foo.class)
                .stream()
                .limit(Constants.SAMPLE_SIZE_DD);

        assertThat(result)
                .map(Foo::value)
                .anyMatch(Objects::nonNull)
                .containsNull();
    }

    @Test
    void dualTargetAnnotationInvokesHandlerExactlyOnce() {
        class Pojo {
            @DualTargetLongValue(123)
            Long value;
        }

        final int before = CustomAnnotationProcessor.getDualTargetHandlerInvocationCount();
        final Pojo result = Instancio.create(Pojo.class);
        final int after = CustomAnnotationProcessor.getDualTargetHandlerInvocationCount();

        assertThat(after - before)
                .as("handler invoked exactly once for dual-target annotation")
                .isEqualTo(1);

        assertThat(result.value).isEqualTo(123L);
    }
}
