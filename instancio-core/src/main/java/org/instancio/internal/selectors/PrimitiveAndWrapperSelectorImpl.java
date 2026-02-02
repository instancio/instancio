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
package org.instancio.internal.selectors;

import org.instancio.Scope;
import org.instancio.ScopeableSelector;
import org.instancio.Selector;
import org.instancio.TargetSelector;
import org.instancio.internal.ApiValidator;
import org.instancio.internal.Flattener;
import org.instancio.internal.util.Fail;
import org.instancio.internal.util.Format;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PrimitiveAndWrapperSelectorImpl implements Selector, Flattener<TargetSelector> {

    private static final Map<Class<?>, String> API_METHOD_NAMES = getApiMethodNames();

    private final SelectorImpl primitive;
    private final SelectorImpl wrapper;

    public PrimitiveAndWrapperSelectorImpl(final Class<?> primitiveType, final Class<?> wrapperType) {
        this.primitive = SelectorImpl.builder()
                .target(new TargetClass(primitiveType))
                .parent(this)
                .build();

        this.wrapper = SelectorImpl.builder()
                .target(new TargetClass(wrapperType))
                .parent(this)
                .build();
    }

    public PrimitiveAndWrapperSelectorImpl(final SelectorImpl primitive, SelectorImpl wrapper) {
        this.primitive = primitive;
        this.wrapper = wrapper;
    }

    public boolean isScoped() {
        return !primitive.getScopes().isEmpty();
    }

    public SelectorImpl getPrimitive() {
        return primitive;
    }

    public SelectorImpl getWrapper() {
        return wrapper;
    }

    @Override
    public ScopeableSelector lenient() {
        return new PrimitiveAndWrapperSelectorImpl(
                primitive.toBuilder().lenient().build(),
                wrapper.toBuilder().lenient().build());
    }

    @Override
    public Selector atDepth(final int depth) {
        ApiValidator.validateDepth(depth);
        return new PrimitiveAndWrapperSelectorImpl(
                primitive.toBuilder().depth(depth).build(),
                wrapper.toBuilder().depth(depth).build());
    }

    @Override
    public List<TargetSelector> flatten() {
        return Arrays.asList(primitive, wrapper);
    }

    @Override
    public Selector within(@NotNull final Scope... scopes) {
        final List<Scope> scopeList = Arrays.asList(scopes);
        return new PrimitiveAndWrapperSelectorImpl(
                primitive.toBuilder().scopes(scopeList).build(),
                wrapper.toBuilder().scopes(scopeList).build());
    }

    @Override
    public Scope toScope() {
        throw Fail.withUsageError("method 'toScope()' is not supported for selector '%s'", this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimitiveAndWrapperSelectorImpl that)) return false;
        return primitive.equals(that.primitive) && wrapper.equals(that.wrapper);
    }

    @Override
    public int hashCode() {
        int result = primitive.hashCode();
        result = 31 * result + wrapper.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String str = API_METHOD_NAMES.get(primitive.getTargetClass());

        // can have either scopes or depth, but not both
        if (!primitive.getScopes().isEmpty()) {
            str += ", " + Format.formatScopes(primitive.getScopes());
        } else if (primitive.getDepth() != null) {
            str += ".atDepth(" + primitive.getDepth() + ")";
        }
        return str;
    }

    private static Map<Class<?>, String> getApiMethodNames() {
        final Map<Class<?>, String> map = new HashMap<>();
        map.put(boolean.class, "allBooleans()");
        map.put(char.class, "allChars()");
        map.put(byte.class, "allBytes()");
        map.put(short.class, "allShorts()");
        map.put(int.class, "allInts()");
        map.put(long.class, "allLongs()");
        map.put(float.class, "allFloats()");
        map.put(double.class, "allDoubles()");
        return Collections.unmodifiableMap(map);
    }
}
