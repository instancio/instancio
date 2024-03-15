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
package org.instancio.internal.assigners;

import org.instancio.exception.InstancioApiException;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetFieldError;
import org.instancio.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.instancio.internal.util.ExceptionUtils.logException;

final class FieldAssigner implements Assigner {
    private static final Logger LOG = LoggerFactory.getLogger(FieldAssigner.class);

    private final Settings settings;

    FieldAssigner(final Settings settings) {
        this.settings = settings;

        LOG.trace("{}, {}", AssignmentType.FIELD, settings.get(Keys.ON_SET_FIELD_ERROR));
    }

    @Override
    public void assign(final InternalNode node, final Object target, final Object value) {
        final Field field = node.getField();

        // can't assign null to a primitive
        if (field != null && (value != null || !field.getType().isPrimitive())) {
            setField(node, target, value);
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    private void setField(final InternalNode node, final Object target, final Object value) {
        try {
            Field field = node.getField();
            if (!field.isAccessible()) { // NOSONAR
                field.setAccessible(true);
            }
            field.set(target, value);
        } catch (IllegalArgumentException ex) {
            // Wrong type is being assigned to a field.
            // Always propagate type mismatch errors as it's most likely a user error.
            String msg = ErrorMessageUtils.getTypeMismatchErrorMessage(value, node, ex);

            throw Fail.withUsageError(msg, ex);
        } catch (Exception ex) {
            handleError(node, value, ex);
        }
    }

    private void handleError(final InternalNode node, final Object value, final Exception ex) {
        final OnSetFieldError onSetFieldError = settings.get(Keys.ON_SET_FIELD_ERROR);
        final Field field = node.getField();
        if (onSetFieldError == OnSetFieldError.FAIL) {
            final String msg = ErrorMessageUtils.incompatibleField(value, field, ex, settings);
            throw new InstancioApiException(msg, ex);
        }

        if (onSetFieldError == OnSetFieldError.IGNORE) {
            logException("{}: error assigning value to field: {}", ex, OnSetFieldError.IGNORE, field);
        }
    }
}
