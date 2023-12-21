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

import org.instancio.quickcheck.internal.config.InstancioQuickcheckConfiguration;
import org.junit.platform.engine.EngineExecutionListener;
import org.junit.platform.engine.support.hierarchical.SameThreadHierarchicalTestExecutorService;

import java.util.concurrent.Future;

public class InstancioQuickcheckTestExecutor extends SameThreadHierarchicalTestExecutorService {
    private final InstancioQuickcheckConfiguration configuration;

    public InstancioQuickcheckTestExecutor(final InstancioQuickcheckConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    public Future<Void> execute(InstancioQuickcheckEngineDescriptor descriptor, EngineExecutionListener listener) {
        return submit(createTestTask(descriptor, listener));
    }

    private TestTask createTestTask(InstancioQuickcheckEngineDescriptor descriptor, EngineExecutionListener listener) {
        return new InstancioQuickcheckTestTask(descriptor, listener, this);
    }

    public InstancioQuickcheckConfiguration getConfiguration() {
        return configuration;
    }
}
