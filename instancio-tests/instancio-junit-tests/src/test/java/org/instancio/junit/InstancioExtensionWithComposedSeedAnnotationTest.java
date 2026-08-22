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
package org.instancio.junit;

import org.instancio.Instancio;
import org.instancio.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies that {@code @Seed} is resolved when it is meta-present
 * on a composed annotation.
 */
@ExtendWith(InstancioExtension.class)
class InstancioExtensionWithComposedSeedAnnotationTest {

    private static final long SEED = 1234;
    private static final long OTHER_SEED = 4567;

    @Test
    @Seed(SEED)
    @Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @interface SeededTest {}

    /**
     * Composed of {@link SeededTest}: {@code @Seed} is meta-present indirectly.
     */
    @SeededTest
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface IndirectlySeededTest {}

    @SeededTest
    void shouldUseSeedFromComposedAnnotation() {
        final Result<String> result = Instancio.of(String.class).asResult();

        assertThat(result.getSeed()).isEqualTo(SEED);
    }

    @IndirectlySeededTest
    void shouldUseSeedFromTransitiveMetaAnnotation() {
        final Result<String> result = Instancio.of(String.class).asResult();

        assertThat(result.getSeed()).isEqualTo(SEED);
    }

    @SeededTest
    @Seed(OTHER_SEED)
    void declaredAnnotationShouldTakePrecedenceOverMetaAnnotation() {
        final Result<String> result = Instancio.of(String.class).asResult();

        assertThat(result.getSeed()).isEqualTo(OTHER_SEED);
    }
}
