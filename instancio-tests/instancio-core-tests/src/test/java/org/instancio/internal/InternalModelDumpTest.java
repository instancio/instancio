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
package org.instancio.internal;

import org.instancio.internal.context.ModelContext;
import org.instancio.test.support.pojo.person.Phone;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class InternalModelDumpTest {

    @Mock
    private Consumer<String> mockConsumer;

    @InjectMocks
    private InternalModelDump internalModelDump;

    @Test
    void consumeWithTraceLoggingAndVerboseModeNotEnabled() {
        final InternalModel<?> mockModel = Mockito.mock(InternalModel.class);
        final ModelContext<?> context = ModelContext.builder(Phone.class).build();

        doReturn(context).when(mockModel).getModelContext();

        internalModelDump.consume(mockModel);

        verifyNoInteractions(mockConsumer);
        verifyNoMoreInteractions(mockModel);
    }

    @Test
    void consumeWithVerbose() {
        final long seed = 123L;

        final ModelContext<?> context = ModelContext.builder(Phone.class)
                .withSeed(seed)
                .withSupplier(field(Phone::getCountryCode), () -> "+123")
                .verbose() // enable verbose
                .build();

        final InternalModel<?> model = new InternalModel<>(context);

        internalModelDump.consume(model);
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        verify(mockConsumer).accept(captor.capture());

        // remove carriage return to prevent test failure on Windows
        final String actual = captor.getValue().replace("\r", "");

        // verify parts of the message excluding stacktrace line with the location
        // of the create() method (since this class is under org.instancio package,
        // so the location will be some line in the JDK)
        assertThat(actual).containsSubsequence(
                """
                         _____              _                       _          __  __             _        _
                        |_   _|            | |                     (_)        |  \\/  |           | |      | |
                          | |   _ __   ___ | |_  __ _  _ __    ___  _   ___   | \\  / |  ___    __| |  ___ | |
                          | |  | '_ \\ / __|| __|/ _` || '_ \\  / __|| | / _ \\  | |\\/| | / _ \\  / _` | / _ \\| |
                         _| |_ | | | |\\__ \\| |_| (_| || | | || (__ | || (_) | | |  | || (_) || (_| ||  __/| |
                        |_____||_| |_||___/ \\__|\\__,_||_| |_| \\___||_| \\___/  |_|  |_| \\___/  \\__,_| \\___||_|
                        ________________________________________________________________________________________

                         -> Instancio model for Phone""",

                """
                        ### Settings

                        Settings[
                        isLockedForModifications: true
                        settingsMap:
                        	'array.elements.nullable': false
                        	'array.max.length': 6
                        	'array.min.length': 2
                        """,

                """
                        ### Nodes

                        Format: <depth:class: field>

                        <0:Phone>
                         ├──<1:Phone: String countryCode>
                         └──<1:Phone: String number>

                         -> Node max depth ........: 1
                         -> Model max depth .......: 8
                         -> Total nodes ...........: 3
                         -> Seed ..................: 123
                        """,
                """
                        ### Selectors

                        Selectors and matching nodes, if any:

                         -> Method: generate(), set(), supply()
                            - field(Phone, "countryCode")
                               \\_ Node[Phone.countryCode, depth=1, type=String]

                        ________________________________________________________________________________________
                        Done. Reminder to remove verbose() from:
                        """
        );
    }
}
