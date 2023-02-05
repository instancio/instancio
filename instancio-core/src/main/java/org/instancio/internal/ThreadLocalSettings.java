/*
 *  Copyright 2022-2023 the original author or authors.
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
package org.instancio.internal;

import org.instancio.documentation.InternalApi;
import org.instancio.settings.Settings;

@InternalApi
public final class ThreadLocalSettings {

    private static final ThreadLocalSettings INSTANCE = new ThreadLocalSettings();

    private static final ThreadLocal<Settings> SETTINGS = new ThreadLocal<>();

    private ThreadLocalSettings() {
        // non-instantiable
    }

    public Settings get() {
        return SETTINGS.get();
    }

    public void remove() {
        SETTINGS.remove();
    }

    public void set(final Settings provider) {
        SETTINGS.set(provider);
    }

    public static ThreadLocalSettings getInstance() {
        return INSTANCE;
    }
}
