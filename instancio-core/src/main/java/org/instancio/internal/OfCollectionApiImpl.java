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
import org.instancio.Model;
import org.instancio.Select;
import org.instancio.internal.context.ModelContext;
import org.instancio.internal.reflect.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.Collection;

public final class OfCollectionApiImpl<T, C extends Collection<T>>
        extends OfClassApiImpl<C>
        implements InstancioOfCollectionApi<C> {

    public OfCollectionApiImpl(final Class<C> collectionType, final Type elementType) {
        super(new ParameterizedTypeImpl(collectionType, elementType));
    }

    private OfCollectionApiImpl(final Model<C> collectionModel) {
        super(collectionModel);
    }

    public static <T, C extends Collection<T>> OfCollectionApiImpl<T, C> fromElementModel(
            final Class<C> collectionType,
            final Model<T> elementModel) {

        final InternalModel<T> model = (InternalModel<T>) elementModel;
        final ModelContext<C> context = ModelContext.<C>builder(collectionType)
                .useModelAsTypeArgument(model.getModelContext())
                .build();

        return new OfCollectionApiImpl<>(new InternalModel<>(context));
    }

    @Override
    public InstancioOfCollectionApi<C> size(final int size) {
        generate(Select.root(), gen -> gen.collection().size(size));
        return this;
    }
}
