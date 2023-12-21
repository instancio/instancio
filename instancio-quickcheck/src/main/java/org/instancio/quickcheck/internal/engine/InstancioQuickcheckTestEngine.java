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
package org.instancio.quickcheck.internal.engine;

import org.instancio.quickcheck.internal.config.DefaultInstancioQuickcheckConfiguration;
import org.instancio.quickcheck.internal.config.InstancioQuickcheckConfiguration;
import org.instancio.quickcheck.internal.discovery.InstancioQuickcheckDiscoverer;
import org.junit.platform.commons.JUnitException;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestEngine;
import org.junit.platform.engine.UniqueId;

public class InstancioQuickcheckTestEngine implements TestEngine {
    static final String ENGINE_ID = "instancio-quickcheck";

    @Override
    public String getId() {
        return ENGINE_ID;
    }

    @Override
    public TestDescriptor discover(EngineDiscoveryRequest discoveryRequest, UniqueId uniqueId) {
        InstancioQuickcheckConfiguration configuration = new DefaultInstancioQuickcheckConfiguration(discoveryRequest.getConfigurationParameters());
        InstancioQuickcheckEngineDescriptor engineDescriptor = new InstancioQuickcheckEngineDescriptor(uniqueId, configuration);
        new InstancioQuickcheckDiscoverer().resolveSelectors(discoveryRequest, engineDescriptor);
        return engineDescriptor;
    }

    @Override
    public void execute(ExecutionRequest request) {
        final InstancioQuickcheckEngineDescriptor descriptor = (InstancioQuickcheckEngineDescriptor) request.getRootTestDescriptor();
        final EngineExecutionListener engineExecutionListener = request.getEngineExecutionListener();

        try (InstancioQuickcheckTestExecutor executorService = new InstancioQuickcheckTestExecutor(descriptor.getConfiguration())) {
            executorService.execute(descriptor, engineExecutionListener).get();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new JUnitException("Error executing tests for engine " + getId(), exception);
        } catch (Exception exception) {
            throw new JUnitException("Error executing tests for engine " + getId(), exception);
        }
    }
}
