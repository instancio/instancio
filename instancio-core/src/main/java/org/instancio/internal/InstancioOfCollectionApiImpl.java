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

import java.util.Collection;

public final class InstancioOfCollectionApiImpl<T, C extends Collection<T>>
        extends InstancioOfClassApiImpl<C>
        implements InstancioOfCollectionApi<C> {

    public InstancioOfCollectionApiImpl(
            final Class<C> collectionType,
            final Class<T> elementType) {

        super(collectionType);
        withTypeParameters(elementType);
    }

    private InstancioOfCollectionApiImpl(final Model<C> collectionModel) {
        super(collectionModel);
    }

    public static <T, C extends Collection<T>> InstancioOfCollectionApiImpl<T, C> fromElementModel(
            final Class<C> collectionType,
            final Model<T> elementModel) {

        final InternalModel<T> model = (InternalModel<T>) elementModel;
        final ModelContext<C> context = ModelContext.<C>builder(collectionType)
                .useModelAsTypeArgument(model.getModelContext())
                .build();

        return new InstancioOfCollectionApiImpl<>(new InternalModel<>(context));
    }

    @Override
    public InstancioOfCollectionApi<C> size(final int size) {
        generate(Select.root(), gen -> gen.collection().size(size));
        return this;
    }
}
