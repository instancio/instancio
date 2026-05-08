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
package org.instancio.test.beanvalidation.adhoc;

import jakarta.validation.Valid;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See: https://github.com/instancio/instancio/issues/1699
 */
@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class GH1699Test {

    @Data
    private static class Wrapper<T> {
        private T value;
    }

    @Data
    private static class Item {
        private String name;
    }

    @Data
    private static class Root {
        @Valid
        private Wrapper<List<@Valid Item>> items;
    }

    @Test
    void shouldNotThrowClassCastExceptionForGenericWrapperWithTypeUseAnnotation() {
        final Root result = Instancio.of(Root.class)
                .withSetting(Keys.BEAN_VALIDATION_ENABLED, true)
                .withSetting(Keys.FAIL_ON_ERROR, true)
                .create();

        assertThat(result).isNotNull();
    }
}
