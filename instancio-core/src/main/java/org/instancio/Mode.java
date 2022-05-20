/*
 * Copyright 2022 the original author or authors.
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

/**
 * The mode is used to specify strictness level, either {@link #STRICT} or {@link #LENIENT},
 * an idea borrowed from the Mockito library.
 * <p>
 * In strict mode, which is enabled by default, unused selectors will trigger an exception.
 * This is done in order to keep tests concise and clean.
 *
 * @since 1.3.3
 */
public enum Mode {

    /**
     * Triggers an exception if at least one selector was not used.
     * This mode is enabled by default.
     *
     * @since 1.3.3
     */
    STRICT,

    /**
     * No exception is triggered on unused selectors.
     *
     * @since 1.3.3
     */
    LENIENT
}
