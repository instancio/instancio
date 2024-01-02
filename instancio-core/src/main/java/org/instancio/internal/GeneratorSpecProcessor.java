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

import org.instancio.documentation.InternalApi;
import org.instancio.generator.GeneratorSpec;
import org.instancio.internal.nodes.InternalNode;
import org.jetbrains.annotations.NotNull;

/**
 * Processes generator specs prior to generating a value,
 * for example, in order to customise spec parameters.
 *
 * @since 2.7.0
 */
@InternalApi
public interface GeneratorSpecProcessor {

    /**
     * Processes given generator spec.
     *
     * @param spec generator spec to process
     * @param node to process
     * @since 2.7.0
     */
    void process(@NotNull GeneratorSpec<?> spec,
                 @NotNull InternalNode node);
}
