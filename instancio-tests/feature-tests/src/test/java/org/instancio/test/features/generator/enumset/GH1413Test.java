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
package org.instancio.test.features.generator.enumset;

import org.instancio.Instancio;
import org.instancio.TypeToken;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.EnumSet;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See https://github.com/instancio/instancio/issues/1413
 */
@FeatureTag(Feature.GENERATOR)
@ExtendWith(InstancioExtension.class)
class GH1413Test {

    private enum EnumWithAnonymousClass {
        ANONYMOUS_CLASS_VALUE {}
    }

    @Test
    void enumValueAnonymousClass() {
        final EnumSet<EnumWithAnonymousClass> result = Instancio.of(new TypeToken<EnumSet<EnumWithAnonymousClass>>() {})
                .withSetting(Keys.FAIL_ON_ERROR, true)
                .create();

        assertThat(result).containsOnly(EnumWithAnonymousClass.ANONYMOUS_CLASS_VALUE);
    }
}
