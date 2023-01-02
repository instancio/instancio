/*
 * Copyright 2022-2023 the original author or authors.
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

package org.instancio.processor.util;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.annotation.processing.Messager;
import javax.tools.Diagnostic;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class LoggerTest {

    @Mock
    private Messager mockMessager;

    @Captor
    private ArgumentCaptor<String> messageCaptor;

    @Nested
    class DebugTest {
        @Test
        void debugNonVerbose() {
            final Logger logger = new Logger(mockMessager, false);
            logger.debug("some message");
            verifyNoInteractions(mockMessager);
        }

        @Test
        void debugVerbose() {
            final Logger logger = new Logger(mockMessager, true);
            logger.debug("some message '%s'", "foo");
            verify(mockMessager).printMessage(eq(Diagnostic.Kind.NOTE), messageCaptor.capture());
            assertThat(messageCaptor.getValue()).isEqualTo("Instancio Processor: some message 'foo'");
        }
    }

    @Nested
    class WarnTest {
        @Test
        void warnVerboseWithoutArgs() {
            final Logger logger = new Logger(mockMessager, true);
            logger.warn("some message");

            verify(mockMessager).printMessage(eq(Diagnostic.Kind.WARNING), messageCaptor.capture());
            assertThat(messageCaptor.getValue()).isEqualTo("Instancio Processor: some message");
        }

        @Test
        void warnVerboseWithoutException() {
            final Logger logger = new Logger(mockMessager, true);
            logger.warn("some message '%s'", "foo");

            verify(mockMessager).printMessage(eq(Diagnostic.Kind.WARNING), messageCaptor.capture());
            assertThat(messageCaptor.getValue()).isEqualTo("Instancio Processor: some message 'foo'");
        }

        @Test
        void warnVerboseWithException() {
            final Logger logger = new Logger(mockMessager, true);
            logger.warn("some message '%s'", "foo", new RuntimeException("ex-msg"));

            verify(mockMessager).printMessage(eq(Diagnostic.Kind.WARNING), messageCaptor.capture());

            assertThat(messageCaptor.getValue())
                    .containsSubsequence("Instancio Processor: some message 'foo'",
                            "\nStacktrace:",
                            "\njava.lang.RuntimeException: ex-msg")
                    .containsPattern("at org\\.instancio.*.warnVerboseWithException.LoggerTest\\.java:\\d+");
        }

        @Test
        void warnNonVerboseWithException() {
            final Logger logger = new Logger(mockMessager, false);
            logger.warn("some message '%s'", "foo", new RuntimeException("ex-msg"));

            verify(mockMessager).printMessage(eq(Diagnostic.Kind.WARNING), messageCaptor.capture());
            assertThat(messageCaptor.getValue())
                    .isEqualTo("Instancio Processor: some message 'foo'. Caused by java.lang.RuntimeException: ex-msg");
        }
    }
}
