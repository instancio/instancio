/*
 *  Copyright 2022 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.instancio;

/**
 * A callback that gets invoked after an object has been fully populated.
 *
 * @param <T> type handled by the callback
 */
public interface OnCompleteCallback<T> {

    /**
     * Method called after object has been populated.
     *
     * @param object fully populated instance to call the callback on
     */
    void onComplete(T object);
}
