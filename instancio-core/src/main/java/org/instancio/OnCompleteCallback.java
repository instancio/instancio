/*
 * Copyright 2022-2025 the original author or authors.
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
 * A callback that gets invoked after an object has been fully populated.
 *
 * @param <T> type handled by the callback
 * @since 1.0.4
 */
@FunctionalInterface
public interface OnCompleteCallback<T> {

    /**
     * A callback method that is called after an object has been populated.
     * <p>
     * If a {@code null} value is generated for a selector with a callback,
     * the callback will not be invoked.
     * <p>
     * In the following snippet, all {@code Phone} instances are nullable
     * and also have a callback associated with them. The callback will only
     * be called when a non-null value is generated.
     *
     * <pre>{@code
     *     Person person = Instancio.of(Person.class)
     *         .withNullable(all(Phone.class))
     *         .onComplete(all(Phone.class), (Phone phone) -> {
     *             // will only be called on non-null phones
     *         })
     *         .create();
     * }</pre>
     *
     * @param object fully populated instance to call the callback on
     * @since 1.0.4
     */
    void onComplete(T object);
}
