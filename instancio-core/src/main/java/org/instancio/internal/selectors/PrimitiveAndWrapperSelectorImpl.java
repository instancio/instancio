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
package org.instancio.internal.selectors;

import org.instancio.Scope;
import org.instancio.Selector;
import org.instancio.exception.InstancioApiException;
import org.instancio.util.Format;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PrimitiveAndWrapperSelectorImpl implements Selector, Flattener {

    private static final Map<Class<?>, String> API_METHOD_NAMES = getApiMethodNames();

    private final SelectorImpl primitive;
    private final SelectorImpl wrapper;

    public PrimitiveAndWrapperSelectorImpl(final Class<?> primitiveType, final Class<?> wrapperType) {
        this.primitive = new SelectorImpl(SelectorTargetKind.CLASS, primitiveType, null, Collections.emptyList(), this);
        this.wrapper = new SelectorImpl(SelectorTargetKind.CLASS, wrapperType, null, Collections.emptyList(), this);
    }

    private PrimitiveAndWrapperSelectorImpl(final SelectorImpl primitive, SelectorImpl wrapper) {
        this.primitive = primitive;
        this.wrapper = wrapper;
    }

    @Override
    public List<SelectorImpl> flatten() {
        return Arrays.asList(primitive, wrapper);
    }

    @Override
    public Selector within(final Scope... scopes) {
        final List<Scope> scopeList = Arrays.asList(scopes);
        return new PrimitiveAndWrapperSelectorImpl(
                new SelectorImpl(SelectorTargetKind.CLASS, primitive.getTargetClass(), null, scopeList, this),
                new SelectorImpl(SelectorTargetKind.CLASS, wrapper.getTargetClass(), null, scopeList, this));
    }

    @Override
    public Scope toScope() {
        throw new InstancioApiException(String.format("toScope() is not supported for this selector (%s, %s)",
                primitive.getTargetClass(), wrapper.getTargetClass()));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof PrimitiveAndWrapperSelectorImpl)) return false;
        final PrimitiveAndWrapperSelectorImpl that = (PrimitiveAndWrapperSelectorImpl) o;
        return Objects.equals(primitive, that.primitive) && Objects.equals(wrapper, that.wrapper);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primitive, wrapper);
    }

    @Override
    public String toString() {
        String str = API_METHOD_NAMES.get(primitive.getTargetClass());
        if (!primitive.getScopes().isEmpty()) {
            str += ", " + Format.scopes(primitive.getScopes());
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
