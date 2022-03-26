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
package org.instancio;

import org.instancio.util.Verify;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * A supplier that provides {@link Type} information. It can be used
 * with generic classes in order to avoid "unchecked assignment" warnings.
 * <p>
 * The following examples both create an instance of @{code Pair} class.
 *
 * <pre>{@code
 *      // Generates an "unchecked assignment" warning
 *      // which can be suppressed with @SuppressWarnings("unchecked")
 *      Pair<Integer, String> pair = Instancio.of(Pair.class)
 *          .withTypeParameters(Integer.class, String.class)
 *          .create();
 *
 *     // This usage avoids the warning
 *     Pair<Integer, String> pair = Instancio.of(new TypeToken<Pair<Integer, String>>() {}).create();
 * }</pre>
 *
 * @param <T> type being supplied.<p>
 *            Required to be present, though not used directly.
 */
public interface TypeToken<T> extends TypeTokenSupplier<T> {

    /**
     * Returns the type to be created.
     *
     * @return type to create
     */
    @Override
    default Type get() {
        Type superClass = getClass().getGenericInterfaces()[0];
        Verify.isTrue(superClass instanceof ParameterizedType, "Missing type parameter");
        return ((ParameterizedType) superClass).getActualTypeArguments()[0];
    }

}