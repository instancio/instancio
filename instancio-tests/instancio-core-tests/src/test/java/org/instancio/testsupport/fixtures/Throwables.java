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
package org.instancio.testsupport.fixtures;

import java.util.Arrays;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class Throwables {

    private Throwables() {
        // non-instantiable
    }

    public static <T extends Throwable> T mockThrowable(String... stackElementClassNames) {
        final StackTraceElement[] stackTraceElements = Arrays.stream(stackElementClassNames)
                .map(Throwables::mockStackTraceElement)
                .toArray(StackTraceElement[]::new);

        return when(mock(Throwable.class).getStackTrace()).thenReturn(stackTraceElements).getMock();
    }

    private static StackTraceElement mockStackTraceElement(final String className) {
        final StackTraceElement mock = mock(StackTraceElement.class);
        when(mock.getClassName()).thenReturn(className);
        when(mock.toString()).thenReturn(className);
        return mock;
    }
}
