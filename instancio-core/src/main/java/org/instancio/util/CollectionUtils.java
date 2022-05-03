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
package org.instancio.util;

import org.instancio.Random;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class CollectionUtils {
    private CollectionUtils() {
        // non-instantiable
    }

    public static void shuffle(final Collection<Object> collection, final Random random) {
        List<Object> list = collection instanceof List
                ? (List<Object>) collection
                : new ArrayList<>(collection);

        for (int i = 0; i < list.size(); i++) {
            final int r = random.intRange(0, i + 1);
            final Object tmp = list.get(i);

            list.set(i, list.get(r));
            list.set(r, tmp);
        }
    }

}
