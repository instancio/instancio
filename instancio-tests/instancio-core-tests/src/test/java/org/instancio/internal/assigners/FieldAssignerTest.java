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
package org.instancio.internal.assigners;

import org.instancio.assignment.OnSetFieldError;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.pojo.person.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FieldAssignerTest {
    private static final Exception EXPECTED_ERROR = new RuntimeException("expected error");

    private static InternalNode getMockNode() {
        final Field mockField = mock(Field.class);
        doReturn(Person.class).when(mockField).getDeclaringClass();
        doReturn(String.class).when(mockField).getType();
        doReturn("name").when(mockField).getName();
        doThrow(EXPECTED_ERROR).when(mockField).setAccessible(true);
        return when(mock(InternalNode.class).getField()).thenReturn(mockField).getMock();
    }

    private static FieldAssigner createAssigner(final OnSetFieldError onSetFieldError) {
        final Settings settings = Settings.create().set(Keys.ON_SET_FIELD_ERROR, onSetFieldError);
        return new FieldAssigner(settings);
    }

    @Test
    void ignoreError() throws IllegalAccessException {
        final InternalNode mockNode = getMockNode();
        final FieldAssigner assigner = createAssigner(OnSetFieldError.IGNORE);
        assigner.assign(mockNode, "any-target", "any-value");

        verify(mockNode.getField()).setAccessible(true);
        verify(mockNode.getField(), never()).set(anyString(), anyString());
    }

    @Test
    void failOnError() {
        final FieldAssigner assigner = createAssigner(OnSetFieldError.FAIL);
        final InternalNode mockNode = getMockNode();

        assertThatThrownBy(() -> assigner.assign(mockNode, "any-target", "any-value"))
                .isExactlyInstanceOf(InstancioApiException.class)
                .hasRootCauseExactlyInstanceOf(EXPECTED_ERROR.getClass())
                .hasMessage(String.format("%n" +
                        "Throwing exception because:%n" +
                        " -> Keys.ON_SET_FIELD_ERROR = OnSetFieldError.FAIL%n" +
                        "%n" +
                        "Error assigning value to field:%n" +
                        " -> Field: String Person.name%n" +
                        " -> Argument type:  String%n" +
                        " -> Argument value: \"any-value\"%n" +
                        "%n" +
                        "Root cause: %n" +
                        " -> java.lang.RuntimeException: expected error%n" +
                        "%n" +
                        "To ignore the error and leave the field uninitialised%n" +
                        " -> Update Keys.ON_SET_FIELD_ERROR setting to: OnSetFieldError.IGNORE%n"));
    }
}
