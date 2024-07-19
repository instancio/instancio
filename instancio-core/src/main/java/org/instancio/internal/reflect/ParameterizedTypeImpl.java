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
package org.instancio.internal.reflect;

import org.instancio.internal.util.Format;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class ParameterizedTypeImpl implements ParameterizedType {

    private final Type ownerType;
    private final Class<?> rawType;
    private final Type[] typeArguments;

    private ParameterizedTypeImpl(final Type ownerType, final Class<?> rawType, final Type... typeArguments) {
        this.ownerType = ownerType;
        this.rawType = rawType;
        this.typeArguments = typeArguments;
    }

    public ParameterizedTypeImpl(final Class<?> rawType, final Type... typeArguments) {
        this(null, rawType, typeArguments);
    }

    @Override
    public Type getOwnerType() {
        return ownerType;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return typeArguments.clone();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterizedTypeImpl)) return false;

        final ParameterizedTypeImpl that = (ParameterizedTypeImpl) o;

        return Objects.equals(ownerType, that.ownerType)
                && Objects.equals(rawType, that.rawType)
                && Arrays.equals(typeArguments, that.typeArguments);
    }

    @Override
    public int hashCode() {
        int result = ownerType != null ? ownerType.hashCode() : 0;
        result = 31 * result + (rawType != null ? rawType.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(typeArguments);
        return result;
    }

    @Override
    public String toString() {
        final String args = Stream.of(typeArguments)
                .map(Format::withoutPackage)
                .collect(Collectors.joining(", "));

        return Format.withoutPackage(rawType) + "<" + args + ">";
    }
}
