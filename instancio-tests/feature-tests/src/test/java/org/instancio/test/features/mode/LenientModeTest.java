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
package org.instancio.test.features.mode;

import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Mode;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.allStrings;

@FeatureTag(Feature.MODE)
@ExtendWith(InstancioExtension.class)
class LenientModeTest {

    @Test
    @DisplayName("Enable lenient mode using the shortcut method")
    void lenient() {
        final Integer result = Instancio.of(Integer.class)
                .lenient()
                .ignore(allStrings()) // unused selector - no error should be thrown
                .create();

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("Enable lenient mode using Settings")
    void lenientViaSettings() {
        final Integer result = Instancio.of(Integer.class)
                .withSettings(Settings.create().set(Keys.MODE, Mode.LENIENT))
                .ignore(allStrings()) // unused selector - no error should be thrown
                .create();

        assertThat(result).isNotNull();
    }
}
