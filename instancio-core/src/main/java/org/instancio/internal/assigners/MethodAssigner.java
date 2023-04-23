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

import org.instancio.assignment.AssignmentType;
import org.instancio.assignment.OnSetMethodError;
import org.instancio.assignment.OnSetMethodNotFound;
import org.instancio.assignment.SetterStyle;
import org.instancio.exception.InstancioApiException;
import org.instancio.exception.InstancioException;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;

import static org.instancio.internal.util.ExceptionHandler.logException;

final class MethodAssigner implements Assigner {
    private static final Logger LOG = LoggerFactory.getLogger(MethodAssigner.class);

    private final Assigner fieldAssigner;
    private final Settings settings;
    private final MethodNameResolver setterNameResolver;

    MethodAssigner(final Settings settings) {
        this.settings = settings;
        this.setterNameResolver = getMethodNameResolver(settings.get(Keys.SETTER_STYLE));
        this.fieldAssigner = new FieldAssigner(settings);

        LOG.trace("{}, {}, {}, {}", AssignmentType.METHOD,
                settings.get(Keys.SETTER_STYLE),
                settings.get(Keys.ON_SET_METHOD_NOT_FOUND),
                settings.get(Keys.ON_SET_METHOD_ERROR));
    }

    private static MethodNameResolver getMethodNameResolver(final SetterStyle style) {
        switch (style) {
            case SET:
                return new SetterMethodNameWithPrefixResolver("set");
            case WITH:
                return new SetterMethodNameWithPrefixResolver("with");
            case PROPERTY:
                return new SetterMethodNameNoPrefix();
            default:
                throw Fail.withFataInternalError("Unhandled setter style: %s", style);
        }
    }

    @Override
    public void assign(final InternalNode node, final Object target, final Object arg) {
        final Field field = node.getField();
        if (arg != null) {
            // can't use setters on final fields
            if (Modifier.isFinal(field.getModifiers())) {
                fieldAssigner.assign(node, target, arg);
            } else {
                assignViaMethod(node, target, arg);
            }
        } else if (!field.getType().isPrimitive()) { // can't assign null to primitives
            assignViaMethod(node, target, null);
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    private void assignViaMethod(final InternalNode node, final Object target, final Object arg) {
        final String methodName = setterNameResolver.resolveFor(node.getField());
        final Optional<Method> methodOpt = resolveSetterMethod(methodName, node.getField());

        if (methodOpt.isPresent()) {
            final Method method = methodOpt.get();
            try {
                method.setAccessible(true);
                method.invoke(target, arg);
            } catch (IllegalAccessException ex) {
                throw new InstancioException("Error setting value via method: " + method, ex);
            } catch (Exception ex) {
                handleMethodInvocationError(node, target, arg, method, ex);
            }
        } else {
            handleMethodNotFoundError(node, target, arg, methodName);
        }
    }

    private void handleMethodInvocationError(
            final InternalNode node,
            final Object target,
            final Object arg,
            final Method method,
            final Exception ex) {

        final OnSetMethodError onSetMethodError = settings.get(Keys.ON_SET_METHOD_ERROR);

        if (onSetMethodError == OnSetMethodError.FAIL) {
            final String methodName = Format.formatMethod(method);
            final String errorMsg = AssignerErrorUtil.getSetterInvocationErrorMessage(
                    arg, methodName, ex, settings);

            throw new InstancioApiException(errorMsg, ex);
        }

        if (onSetMethodError == OnSetMethodError.ASSIGN_FIELD) {
            logException("Error invoking method {}, assigning value using field: {}",
                    ex, method, node.getField());

            fieldAssigner.assign(node, target, arg);
        } else if (onSetMethodError == OnSetMethodError.IGNORE) {
            logException("{}: error invoking method: {}", ex, OnSetMethodError.IGNORE, method);
        }
    }

    private void handleMethodNotFoundError(
            final InternalNode node,
            final Object target,
            final Object arg,
            final String methodName) {

        final OnSetMethodNotFound onSetMethodNotFound = settings.get(Keys.ON_SET_METHOD_NOT_FOUND);

        if (onSetMethodNotFound == OnSetMethodNotFound.FAIL) {
            throw new InstancioApiException(AssignerErrorUtil.setterNotFound(
                    node.getField(), methodName, settings));
        }

        if (onSetMethodNotFound == OnSetMethodNotFound.ASSIGN_FIELD) {
            LOG.trace("Could not resolve setter method, assigning value using field: {}", node.getField());
            fieldAssigner.assign(node, target, arg);
        } else if (onSetMethodNotFound == OnSetMethodNotFound.IGNORE) {
            LOG.debug("{}: class {} has no setter method: {}", OnSetMethodNotFound.IGNORE,
                    target.getClass().getName(), methodName);
        }
    }

    private Optional<Method> resolveSetterMethod(final String methodName, final Field field) {
        if (methodName != null) {
            try {
                final Class<?> klass = field.getDeclaringClass();
                return Optional.of(klass.getDeclaredMethod(methodName, field.getType()));
            } catch (NoSuchMethodException ex) {
                logException("Resolved setter method '{}' for field '{}' does not exist", ex, methodName, Format.formatField(field));
            }
        }
        return Optional.empty();
    }
}
