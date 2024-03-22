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
package org.external.validation;

import org.example.spi.CustomAnnotationProcessor;
import org.example.spi.CustomAnnotationProcessor.AnnotationForHandlerWithTooFewArgs;
import org.example.spi.CustomAnnotationProcessor.AnnotationForHandlerWithTooManyArgs;
import org.example.spi.CustomAnnotationProcessor.PastLocalDate;
import org.instancio.Instancio;
import org.instancio.exception.InstancioApiException;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.catchThrowable;

/**
 * Tests for annotation processor SPI validation.
 *
 * @see CustomAnnotationProcessor
 */
@ExtendWith(InstancioExtension.class)
class AnnotationProcessorSpiValidationTest {

    /**
     * The {@code @AnnotationHandler} method specifies invalid
     * generator spec in the method signature.
     */
    @Test
    void handlerWithInvalidSpec() {
        class Pojo {
            @PastLocalDate
            LocalDate value;
        }

        Throwable ex = catchThrowable(() -> Instancio.create(Pojo.class));

        assertThat(ex).isExactlyInstanceOf(InstancioApiException.class);

        assertThat(normalised(ex.getMessage())).isEqualTo(normalised("""
                Error creating an object
                 -> at org.external.validation.AnnotationProcessorSpiValidationTest.lambda$handlerWithInvalidSpec$0(AnnotationProcessorSpiValidationTest.java:53)

                Reason: invalid @AnnotationHandler method defined by org.example.spi.CustomAnnotationProcessor$AnnotationProcessorImpl

                    @AnnotationHandler
                    handlerWithInvalidGeneratorSpecParameter(CustomAnnotationProcessor$PastLocalDate, InstantSpec)

                 -> Annotation ...............: CustomAnnotationProcessor$PastLocalDate
                 -> Annotated type ...........: LocalDate
                 -> Resolved generator spec ..: LocalDateGenerator
                 -> Specified generator ......: InstantSpec (not valid)
                 -> Matched node .............: field AnnotationProcessorSpiValidationTest$1Pojo.value (depth=1)

                 │ Path to root:
                 │   <1:AnnotationProcessorSpiValidationTest$1Pojo: LocalDate value>
                 │    └──<0:AnnotationProcessorSpiValidationTest$1Pojo>   <-- Root
                 │
                 │ Format: <depth:class: field>

                To resolve this issue:

                 -> check if the annotated type is compatible with the annotation
                 -> update the @AnnotationHandler method signature by specifying the correct generator spec class

                The accepted signatures for @AnnotationHandler methods are:

                 -> void example(Annotation annotation, GeneratorSpec<?> spec)
                 -> void example(Annotation annotation, GeneratorSpec<?> spec, Node node)

                where:

                 - 'annotation' and 'spec' parameters can be subtypes of Annotation and GeneratorSpec, respectively.
                 - 'node' parameter is optional.

                Example:

                  @Retention(RetentionPolicy.RUNTIME)
                  public @interface HexString {
                      int length();
                  }

                  @AnnotationHandler
                  void handleZipCode(HexString annotation, StringGeneratorSpec spec) {
                      spec.hex().length(annotation.length());
                  }
                """));
    }

    @Test
    void handlerWithTooFewArgs() {
        class Pojo {
            @AnnotationForHandlerWithTooFewArgs
            LocalDate value;
        }

        assertThatThrownBy(() -> Instancio.create(Pojo.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "handlerWithTooFewArgs",
                        "Invalid number of method parameters: 1",
                        "The accepted signatures for @AnnotationHandler methods are:");
    }

    @Test
    void handlerWithTooManyArgs() {
        class Pojo {
            @AnnotationForHandlerWithTooManyArgs
            LocalDate value;
        }

        assertThatThrownBy(() -> Instancio.create(Pojo.class))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasMessageContainingAll(
                        "handlerWithTooManyArgs",
                        "Invalid number of method parameters: 4",
                        "The accepted signatures for @AnnotationHandler methods are:");
    }

    private static String normalised(String s) {
        return s.replace("\r", "").trim();
    }
}
