/*
 * Copyright 2022-2026 the original author or authors.
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
package org.instancio.test.support;

import org.instancio.Instancio;
import org.instancio.InstancioApi;

import java.util.function.Consumer;

public final class Api {

    @SafeVarargs
    public static <T> InstancioApi<T> shuffleApiOrder(
            final Class<T> klass,
            final Consumer<InstancioApi<T>>... apiCalls) {

        final var instancioApi = Instancio.of(klass);
        final var shuffledApiCalls = Instancio.gen().shuffle(apiCalls).get();

        for (var apiCall : shuffledApiCalls) {
            apiCall.accept(instancioApi);
        }
        return instancioApi;
    }

    private Api() {
        // non-instantiable
    }
}
