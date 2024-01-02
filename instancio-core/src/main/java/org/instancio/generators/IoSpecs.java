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
package org.instancio.generators;

import org.instancio.generator.specs.FileSpec;
import org.instancio.internal.generator.io.FileGenerator;

import java.io.File;

/**
 * Provides generators for {@code java.io} classes.
 *
 * @since 2.6.0
 */
public final class IoSpecs {

    /**
     * Generates {@link File} values.
     *
     * @return API builder reference
     * @since 2.6.0
     */
    public FileSpec file() {
        return new FileGenerator();
    }
}
