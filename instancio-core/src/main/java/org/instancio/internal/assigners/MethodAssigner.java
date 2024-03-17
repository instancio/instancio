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
import org.instancio.exception.InstancioException;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.nodes.InternalNode;
import org.instancio.internal.util.ErrorMessageUtils;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.MethodUtils;
import org.instancio.internal.util.ReflectionUtils;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodError;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.instancio.internal.util.ExceptionUtils.logException;

final class MethodAssigner implements Assigner {
    private static final Logger LOG = LoggerFactory.getLogger(MethodAssigner.class);

    private final Settings settings;
    private final int excludedModifiers;
    private final Assigner fieldAssigner;
    private final SetterMethodResolverFacade setterMethodResolverFacade;

    MethodAssigner(final ModelContext<?> context) {
        this.settings = context.getSettings();
        this.excludedModifiers = settings.get(Keys.SETTER_EXCLUDE_MODIFIER);
        this.fieldAssigner = new FieldAssigner(settings);
        this.setterMethodResolverFacade = new SetterMethodResolverFacade(
                context.getServiceProviders().getSetterMethodResolvers());

        LOG.trace("{}, {}, {}, {}", AssignmentType.METHOD,
                settings.get(Keys.SETTER_STYLE),
                settings.get(Keys.ON_SET_METHOD_NOT_FOUND),
                settings.get(Keys.ON_SET_METHOD_ERROR));
    }

    @Override
    public void assign(final InternalNode node, final Object target, final Object arg) {
        final Method method = getSetterMethod(node);

        if (method != null) {
            if (MethodUtils.isExcluded(method.getModifiers(), excludedModifiers)) {
                return;
            }

            try {
                // can't assign null to a primitive
                if (arg != null || !method.getParameterTypes()[0].isPrimitive()) {
                    ReflectionUtils.setAccessible(method);
                    method.invoke(target, arg);
                }
            } catch (IllegalAccessException ex) {
                throw new InstancioException("Error setting value via method: " + method, ex);
            } catch (Exception ex) {
                handleMethodInvocationError(node, target, arg, method, ex);
            }
        } else {
            handleMethodNotFoundError(node, target, arg);
        }
    }

    private Method getSetterMethod(final InternalNode node) {
        final Method method = setterMethodResolverFacade.resolveSetterMethod(node);
        return method == null ? node.getSetter() : method;
    }

    private void handleMethodInvocationError(
            final InternalNode node,
            final Object target,
            final Object arg,
            final Method method,
            final Exception ex) {

        final OnSetMethodError onSetMethodError = settings.get(Keys.ON_SET_METHOD_ERROR);

        if (onSetMethodError == OnSetMethodError.FAIL) {
            final String methodName = Format.formatSetterMethod(method);
            final String errorMsg = ErrorMessageUtils.getSetterInvocationErrorMessage(
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
            final Object arg) {

        final OnSetMethodNotFound onSetMethodNotFound = settings.get(Keys.ON_SET_METHOD_NOT_FOUND);

        if (onSetMethodNotFound == OnSetMethodNotFound.ASSIGN_FIELD) {
            LOG.trace("Could not resolve setter method, assigning value using field: {}", node.getField());
            fieldAssigner.assign(node, target, arg);
            return;
        }

        // do not log or throw an error since final fields cannot have setters
        if (Modifier.isFinal(node.getField().getModifiers())) {
            return;
        }

        if (onSetMethodNotFound == OnSetMethodNotFound.FAIL) {
            throw new InstancioApiException(ErrorMessageUtils.setterNotFound(node, settings));
        } else if (onSetMethodNotFound == OnSetMethodNotFound.IGNORE) {
            LOG.debug("{}: could not resolve setter method for field: {}", OnSetMethodNotFound.IGNORE, node.getField());
        }
    }

}
