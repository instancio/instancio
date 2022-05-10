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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class PrimitiveAndWrapperSelectorImpl implements Selector, Flattener {

    private SelectorImpl primitive;
    private SelectorImpl wrapper;

    public PrimitiveAndWrapperSelectorImpl(final Class<?> primitiveType, final Class<?> wrapperType) {
        this.primitive = new SelectorImpl(SelectorTargetType.CLASS, primitiveType, null);
        this.wrapper = new SelectorImpl(SelectorTargetType.CLASS, wrapperType, null);
    }

    @Override
    public List<SelectorImpl> flatten() {
        return Arrays.asList(primitive, wrapper);
    }

    @Override
    public Selector within(final Scope... scopes) {
        final List<Scope> scopeList = Collections.unmodifiableList(Arrays.asList(scopes));
        primitive = new SelectorImpl(SelectorTargetType.CLASS, primitive.getTargetClass(), null, scopeList);
        wrapper = new SelectorImpl(SelectorTargetType.CLASS, wrapper.getTargetClass(), null, scopeList);
        return this;
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
}
