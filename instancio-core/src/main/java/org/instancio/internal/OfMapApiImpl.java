/*
 * Copyright 2022-2023 the original author or authors.
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

import org.instancio.InstancioOfCollectionApi;
import org.instancio.Select;
import org.instancio.internal.reflect.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.Map;

public final class OfMapApiImpl<K, V, M extends Map<K, V>>
        extends OfClassApiImpl<M>
        implements InstancioOfCollectionApi<M> {

    public OfMapApiImpl(final Class<M> mapType, final Type keyType, final Type valueType) {
        super(new ParameterizedTypeImpl(mapType, keyType, valueType));
    }

    @Override
    public InstancioOfCollectionApi<M> size(final int size) {
        generate(Select.root(), gen -> gen.map().size(size));
        return this;
    }
}
