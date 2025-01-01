/*
 * Copyright 2022-2025 the original author or authors.
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
package org.instancio.internal.nodes;

import org.instancio.internal.util.MethodUtils;
import org.instancio.internal.util.SetterMethodComparator;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.SetterStyle;
import org.instancio.settings.Settings;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Collects fields and setters from a given class.
 */
class DeclaredAndInheritedMemberCollector {

    private static final Comparator<Method> METHOD_COMPARATOR = new SetterMethodComparator();

    private final PackageFilter packageFilter = new DefaultPackageFilter();
    private final DefaultSetterMethodResolver defaultSetterMethodResolver;
    private final boolean isMethodAssignmentEnabled;
    private final boolean ignoreUnmatchedSetters;
    private final String setterPrefix;
    private final int setterExcludeModifiers;

    DeclaredAndInheritedMemberCollector(final Settings settings) {
        this.isMethodAssignmentEnabled = settings.get(Keys.ASSIGNMENT_TYPE) == AssignmentType.METHOD;
        this.ignoreUnmatchedSetters = settings.get(Keys.ON_SET_METHOD_UNMATCHED) == OnSetMethodUnmatched.IGNORE;
        this.setterPrefix = getSetterPrefix(settings.get(Keys.SETTER_STYLE));
        this.setterExcludeModifiers = settings.get(Keys.SETTER_EXCLUDE_MODIFIER);
        this.defaultSetterMethodResolver = new DefaultSetterMethodResolver(settings);
    }

    ClassData getClassData(final InternalNode node) {
        final Class<?> klass = node.getTargetClass();
        final List<Field> fields = getNonStaticFields(klass);
        final boolean isRecord = node.getNodeKind() == NodeKind.RECORD;
        final Set<Method> unmatchedSetters = isRecord ? Collections.emptySet() : getSetters(klass);
        final List<MemberPair> memberPairs = new ArrayList<>();

        for (Field field : fields) {
            Method matchedSetter = null;

            // skip records/final fields since they can't have setters
            if (!isRecord && isMethodAssignmentEnabled && !Modifier.isFinal(field.getModifiers())) {
                matchedSetter = defaultSetterMethodResolver.getSetter(field);

                if (matchedSetter != null) {
                    unmatchedSetters.remove(matchedSetter);
                }
            }

            memberPairs.add(new MemberPair(field, matchedSetter));
        }

        return ignoreUnmatchedSetters
                ? new ClassData(memberPairs, Collections.emptySet())
                : new ClassData(memberPairs, unmatchedSetters);
    }

    private List<Field> getNonStaticFields(final Class<?> klass) {
        Class<?> nextClass = klass;

        final List<Field> collected = new ArrayList<>();
        while (shouldCollectFrom(nextClass)) {
            for (Field field : nextClass.getDeclaredFields()) {
                if (!Modifier.isStatic(field.getModifiers())) {
                    collected.add(field);
                }
            }
            nextClass = nextClass.getSuperclass();
        }
        return collected;
    }

    private boolean shouldCollectFrom(@Nullable final Class<?> c) {
        return c != null
                && !c.isInterface()
                && !c.isArray()
                && c != Object.class
                && !packageFilter.isExcluded(c.getPackage());
    }

    /**
     * Ignore setters without a prefix (i.e. {@link SetterStyle#PROPERTY}) when
     * collecting unmatched setters (those that didn't match with a field),
     * since there's no way to tell if a method is an actual setter,
     * e.g. {@code Photo.removeTag(String)} would be a false positive.
     */
    private String getSetterPrefix(final SetterStyle setterStyle) {
        if (setterStyle == SetterStyle.SET) {
            return "set";
        } else if (setterStyle == SetterStyle.WITH) {
            return "with";
        } else {
            return null;
        }
    }

    private Set<Method> getSetters(final Class<?> klass) {
        // Ignore methods without a prefix since we can't tell
        // which method is actually a setter
        if (!isMethodAssignmentEnabled || setterPrefix == null) {
            return Collections.emptySet();
        }

        // Methods are sorted because the order returned by getDeclaredMethods()
        // is not guaranteed. This can lead to nodes being populated in different
        // order and affect reproducibility for a given seed.
        // (getDeclaredFields() order is also not guaranteed but seems to be consistent).
        final Set<Method> collected = new TreeSet<>(METHOD_COMPARATOR);
        final Set<MethodKey> seenMethods = new HashSet<>();

        Class<?> nextClass = klass;
        while (shouldCollectFrom(nextClass)) {
            for (Method method : nextClass.getDeclaredMethods()) {
                final int modifiers = method.getModifiers();

                if (method.getParameterCount() == 1
                        && !MethodUtils.isExcluded(modifiers, setterExcludeModifiers)
                        && method.getName().startsWith(setterPrefix)) {

                    final MethodKey methodKey = new MethodKey(method);

                    // If an overridden method was already collected,
                    // ensure we don't add the same method from the superclass
                    // to avoid multiple invocations of the same method
                    if (seenMethods.add(methodKey)) {
                        collected.add(method);
                    }
                }
            }
            nextClass = nextClass.getSuperclass();
        }
        return collected;
    }

    static final class MethodKey {
        private final String name;
        private final Class<?> parameterType;

        private MethodKey(final Method method) {
            this.name = method.getName();
            this.parameterType = method.getParameterTypes()[0];
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof MethodKey)) return false;

            final MethodKey methodKey = (MethodKey) o;
            return name.equals(methodKey.name) && parameterType.equals(methodKey.parameterType);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + parameterType.hashCode();
            return result;
        }
    }
}
