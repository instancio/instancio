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
package org.instancio.internal;

import org.instancio.InstancioApi;
import org.instancio.InstancioClassApi;
import org.instancio.Model;

import java.lang.reflect.Type;

public class ClassApiImpl<T> extends ApiImpl<T> implements InstancioClassApi<T> {

    public ClassApiImpl(final Type klass) {
        super(klass);
    }

    ClassApiImpl(final Model<T> model) {
        super(model);
    }

    @Override
    public InstancioApi<T> withTypeParameters(final Class<?>... type) {
        super.addTypeParameters(type);
        return this;
    }
}
