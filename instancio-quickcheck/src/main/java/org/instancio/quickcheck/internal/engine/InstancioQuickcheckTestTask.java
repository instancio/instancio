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
package org.instancio.quickcheck.internal.engine;

import org.instancio.internal.util.Sonar;
import org.instancio.quickcheck.api.Property;
import org.instancio.quickcheck.internal.arbitrary.ArbitrariesResolver;
import org.instancio.quickcheck.internal.descriptor.InstancioClassBasedTestDescriptor;
import org.instancio.quickcheck.internal.descriptor.InstancioQuickcheckTestMethodTestDescriptor;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestExecutorService.TestTask;
import org.junit.platform.engine.support.hierarchical.Node.ExecutionMode;
import org.junit.platform.engine.support.hierarchical.ResourceLock;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toCollection;

public class InstancioQuickcheckTestTask implements TestTask {
    private final Map<TestDescriptor, ResourceLock> resourceLocksByTestDescriptor = new HashMap<>();
    private final TestDescriptor descriptor;
    private final EngineExecutionListener listener;
    private final Runnable finalizer;
    private final InstancioQuickcheckTestExecutor executor;
    private boolean started;

    public InstancioQuickcheckTestTask(TestDescriptor descriptor, EngineExecutionListener listener, InstancioQuickcheckTestExecutor executor) {
        this.descriptor = descriptor;
        this.listener = listener;
        this.executor = executor;
        this.finalizer = () -> {
        };
    }

    @Override
    public ExecutionMode getExecutionMode() {
        return ExecutionMode.SAME_THREAD;
    }

    @Override
    public ResourceLock getResourceLock() {
        return resourceLocksByTestDescriptor.getOrDefault(descriptor, new ResourceLock() {
            @Override
            public ResourceLock acquire() {
                return this;
            }

            @Override
            public void release() {
                // nothing to do
            }
        });
    }

    @Override
    @SuppressWarnings({"PMD.AvoidCatchingThrowable", Sonar.CATCH_EXCEPTION_INSTEAD_OF_THROWABLE})
    public void execute() {
        try {
            listener.executionStarted(descriptor);
            started = true;

            executeInternal();

            final List<InstancioQuickcheckTestTask> children = descriptor
                    .getChildren()
                    .stream()
                    .map(desc -> new InstancioQuickcheckTestTask(desc, listener, executor))
                    .collect(toCollection(ArrayList::new));

            executor.invokeAll(children);

            reportCompletion();
        } catch (final Throwable t) {
            reportFailure(t);
        } finally {
            finalizer.run();
        }
    }

    private void reportFailure(Throwable t) {
        if (!started) {
            // Call executionStarted first to comply with the contract of EngineExecutionListener.
            listener.executionStarted(descriptor);
        }

        listener.executionFinished(descriptor, TestExecutionResult.failed(t));
    }

    private void executeInternal() {
        if (descriptor instanceof InstancioQuickcheckTestMethodTestDescriptor) {
            final InstancioQuickcheckTestMethodTestDescriptor desc = (InstancioQuickcheckTestMethodTestDescriptor) descriptor;
            final Method method = desc.getTestMethod();

            final PropertyConfiguration configuration = extractPropertyConfiguration(method);
            final List<Parameter> parameters = Arrays.stream(method.getParameters()).collect(Collectors.toList());

            final Object instance = desc.getParent()
                    .filter(InstancioClassBasedTestDescriptor.class::isInstance)
                    .map(InstancioClassBasedTestDescriptor.class::cast)
                    .map(InstancioClassBasedTestDescriptor::createTestInstance)
                    .orElseThrow(() -> new JUnitException("Property method descriptors should have parent"));

            final ArbitrariesResolver resolver = new ArbitrariesResolver(parameters, executor.getConfiguration());
            for (int i = 0; i < configuration.getSamples(); ++i) {
                final Object[] args = resolver.resolve(instance);
                ReflectionSupport.invokeMethod(method, instance, args);
            }
        }
    }

    private PropertyConfiguration extractPropertyConfiguration(Method method) {
        final Property property = method.getAnnotation(Property.class);
        return new PropertyConfiguration(property.samples());
    }

    private void reportCompletion() {
        if (!started) {
            // Call executionStarted first to comply with the contract of EngineExecutionListener.
            listener.executionStarted(descriptor);
        }

        listener.executionFinished(descriptor, TestExecutionResult.successful());
    }
}
