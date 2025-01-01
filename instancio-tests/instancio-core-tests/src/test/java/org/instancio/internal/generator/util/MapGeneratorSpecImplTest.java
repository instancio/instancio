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
package org.instancio.internal.generator.util;

import org.instancio.Random;
import org.instancio.exception.InstancioException;
import org.instancio.generator.GeneratorContext;
import org.instancio.settings.Settings;
import org.instancio.support.DefaultRandom;
import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MapGeneratorSpecImplTest {

    private final Random random = new DefaultRandom();
    private final GeneratorContext context = new GeneratorContext(Settings.defaults(), random);

    private final MapGeneratorSpecImpl<?, ?> generator = new MapGeneratorSpecImpl<>(context);

    @Test
    void shouldFailIfNoSubtypeIsProvided() {
        assertThatThrownBy(() -> generator.generate(random))
                .isExactlyInstanceOf(InstancioException.class)
                .hasMessage("%s should delegate to another generator", generator.getClass());
    }

    @Test
    void shouldCreateANewInstanceIfSubtypeIsProvided() {
        generator.subtype(TreeMap.class);

        assertThat(generator.generate(random)).isInstanceOf(TreeMap.class);
    }
}
