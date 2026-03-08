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
package org.instancio.junit.internal;

import org.instancio.settings.Settings;
import org.instancio.support.ThreadLocalRandom;
import org.instancio.support.ThreadLocalSettings;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ExtensionSupportTest {

    @Mock
    private ExtensionContext context;

    @AfterEach
    void cleanup() {
        ThreadLocalRandom.getInstance().remove();
        ThreadLocalSettings.getInstance().remove();
    }

    @Test
    void processAnnotations_shouldClearThreadLocalSettings_whenNoWithSettingsAnnotationPresent() {
        ThreadLocalSettings.getInstance().set(Settings.create());

        doReturn(Optional.of(NoSettingsTest.class)).when(context).getTestClass();

        ExtensionSupport.processAnnotations(
                context,
                ThreadLocalRandom.getInstance(),
                ThreadLocalSettings.getInstance());

        assertThat(ThreadLocalSettings.getInstance().get()).isNull();
    }

    /**
     * Dummy test class.
     */
    private static class NoSettingsTest {
        // does not have `@WithSettings` annotated field
    }
}
