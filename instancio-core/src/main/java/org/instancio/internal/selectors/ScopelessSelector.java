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

import org.instancio.TargetSelector;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Member;
import java.util.Objects;

public final class ScopelessSelector implements TargetSelector {
    private final Class<?> targetClass;
    private final Member member; // Field or Method

    public ScopelessSelector(final Class<?> targetClass, @Nullable final Member member) {
        this.targetClass = targetClass;
        this.member = member;
    }

    public ScopelessSelector(final Class<?> targetClass) {
        this(targetClass, null);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof ScopelessSelector selector)) return false;
        return Objects.equals(targetClass, selector.targetClass)
                && Objects.equals(member, selector.member);
    }

    @Override
    public int hashCode() {
        int result = targetClass == null ? 0 : targetClass.hashCode();
        result = 31 * result + (member == null ? 0 : member.hashCode());
        return result;
    }
}
