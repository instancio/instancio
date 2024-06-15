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
package org.instancio;

import org.instancio.settings.Settings;

/**
 * Provides support for verbose mode.
 *
 * @since 4.0.0
 */
interface VerboseMode {

    /**
     * Outputs debug information to {@code System.out}. This includes:
     *
     * <ul>
     *   <li>current {@link Settings}</li>
     *   <li>node hierarchy, including the type and depth of each node</li>
     *   <li>seed used to create the object</li>
     * </ul>
     *
     * <p><b>Warning:</b> this method has a significant performance impact.
     * It is recommended to remove the call to {@code verbose()} after
     * troubleshooting is complete.
     *
     * @return API builder reference
     * @since 4.0.0
     */
    VerboseMode verbose();

}
