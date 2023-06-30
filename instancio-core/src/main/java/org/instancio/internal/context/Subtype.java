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
package org.instancio.internal.context;

import org.jetbrains.annotations.NotNull;

/**
 * The only purpose of this class is to track whether the subtype is
 * provided by a generator (some generators specify a subtype implicitly).
 * This information is used downstream to decide how to handle invalid
 * subtype error, i.e. if the subtype is specified via a generator.
 * let the assignment fail with a "type mismatch error" because
 * "invalid subtype" error would be confusing in such cases.
 */
public final class Subtype {
    private final Class<?> subtypeClass;
    private final boolean viaGenerator;

    public Subtype(@NotNull final Class<?> subtypeClass,
                   final boolean viaGenerator) {
        this.subtypeClass = subtypeClass;
        this.viaGenerator = viaGenerator;
    }

    public Subtype(@NotNull final Class<?> subtypeClass) {
        this(subtypeClass, false);
    }

    public Class<?> getSubtypeClass() {
        return subtypeClass;
    }

    public boolean isViaGenerator() {
        return viaGenerator;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtype)) return false;

        final Subtype subtype = (Subtype) o;

        if (viaGenerator != subtype.viaGenerator) return false;
        return subtypeClass.equals(subtype.subtypeClass);
    }

    @Override
    public int hashCode() {
        int result = subtypeClass.hashCode();
        result = 31 * result + (viaGenerator ? 1 : 0);
        return result;
    }
}
