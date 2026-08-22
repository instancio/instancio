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
import org.instancio.support.DefaultRandom;
import org.instancio.support.InternalTestContext;
import org.instancio.support.ThreadLocalTestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstances;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class ExtensionSupportTest {

    @Mock
    private ExtensionContext context;

    @Mock
    private TestInstances testInstances;

    @AfterEach
    void cleanup() {
        ThreadLocalTestContext.getInstance().remove();
    }

    @Test
    void processAnnotations_shouldClearThreadLocalSettings_whenNoWithSettingsAnnotationPresent() {
        ThreadLocalTestContext.getInstance().set(
                new InternalTestContext(new DefaultRandom(), Settings.create()));

        doReturn(testInstances).when(context).getRequiredTestInstances();
        doReturn(List.of(new NoSettingsTest())).when(testInstances).getAllInstances();

        ExtensionSupport.processAnnotations(context, ThreadLocalTestContext.getInstance());

        assertThat(requireNonNull(ThreadLocalTestContext.getInstance().get()).getSettings()).isNull();
    }

    /**
     * Dummy test class.
     */
    private static class NoSettingsTest {
        // does not have `@WithSettings` annotated field
    }
}
