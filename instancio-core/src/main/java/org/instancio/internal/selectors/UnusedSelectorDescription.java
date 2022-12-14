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
package org.instancio.internal.selectors;

import org.jetbrains.annotations.ApiStatus;

/**
 * Provides selector description in the "unused selectors" error message.
 * <p>
 * This interface is an internal interface, not part of the public API.
 *
 * @since 1.6.0
 */
@ApiStatus.Internal
public interface UnusedSelectorDescription {

    /**
     * Returns selector description, including line number where it was used.
     *
     * @return selector description
     * @since 1.6.0
     */
    String getDescription();
}
