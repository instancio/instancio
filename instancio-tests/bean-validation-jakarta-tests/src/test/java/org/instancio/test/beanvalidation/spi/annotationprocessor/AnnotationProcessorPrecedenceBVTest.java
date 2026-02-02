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
import org.annotations.CollectionSizeOne;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.junit.WithSettings;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(InstancioExtension.class)
class AnnotationProcessorPrecedenceBVTest {

    private static final int SIZE = 10;

    @WithSettings
    private final Settings settings = Settings.create()
            .set(Keys.COLLECTION_NULLABLE, false);

    @Test
    void withCollectionSizeOneFirst() {
        class Pojo {
            @CollectionSizeOne // first
            @Size(min = SIZE, max = SIZE)
            List<String> list;
        }

        final Pojo result = Instancio.create(Pojo.class);
        assertThat(result.list).hasSize(1);
    }

    @Test
    void withCollectionSizeOneLast() {
        class Pojo {
            @Size(min = SIZE, max = SIZE)
            @CollectionSizeOne // last
            List<String> list;
        }

        final Pojo result = Instancio.create(Pojo.class);
        assertThat(result.list).hasSize(1);
    }
}
