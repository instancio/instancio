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
package org.instancio.junit.internal;

import org.instancio.Instancio;
import org.instancio.InstancioApi;
import org.instancio.InstancioFeedApi;
import org.instancio.Random;
import org.instancio.feed.Feed;
import org.instancio.internal.util.TypeUtils;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.lang.reflect.Type;

public final class ExtensionObjectFactory {

    @SuppressWarnings("rawtypes")
    public static Object createObject(final Type type, final Random random, final Settings settings) {
        final Class rawType = TypeUtils.getRawType(type);

        if (Feed.class.isAssignableFrom(rawType)) {
            final InstancioFeedApi<?> api = Instancio.ofFeed(rawType);
            if (settings != null) {
                api.withSettings(settings);
            }
            return api
                    .withSetting(Keys.SEED, random.longRange(1, Long.MAX_VALUE))
                    .create();
        } else {
            final InstancioApi<?> api = Instancio.of(() -> type);
            if (settings != null) {
                api.withSettings(settings);
            }
            return api
                    .withSeed(random.longRange(1, Long.MAX_VALUE))
                    .create();
        }
    }

    private ExtensionObjectFactory() {
        // non-instantiable
    }
}
