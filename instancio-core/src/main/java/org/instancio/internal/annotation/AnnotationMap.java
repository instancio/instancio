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
package org.instancio.internal.annotation;

import org.instancio.internal.util.CollectionUtils;
import org.instancio.internal.util.Verify;
import org.jetbrains.annotations.VisibleForTesting;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * A field can declare annotations from different providers, for example:
 *
 * <ul>
 *   <li>{@code @Email @Size}   - both from Jakarta</li>
 *   <li>{@code @Email @Length} - Jakarta and Hibernate</li>
 * </ul>
 *
 * <p> In addition, annotations can be declared in an arbitrary order:
 * <ul>
 *   <li>{@code @NotNull @Email @Length}</li>
 *   <li>{@code @Length @NotNull @Email}</li>
 * </ul>
 * <p>
 * This map helps keep track of the {@code primary} annotation
 * and provides convenience methods for consuming annotations.
 */
public class AnnotationMap {

    private final Map<Class<?>, Annotation> map;
    private Annotation primary;

    public AnnotationMap(final Annotation... annotations) {
        this.map = CollectionUtils.asLinkedHashMap(Annotation::annotationType, annotations);
    }

    @SuppressWarnings("unchecked")
    <A extends Annotation> A get(final Class<?> key) {
        return (A) map.get(key);
    }

    Collection<Annotation> getAnnotations() {
        final Collection<Annotation> values = map.values();
        return values.isEmpty() ? Collections.emptyList() : new ArrayList<>(values);
    }

    public void setPrimary(final Annotation annotation) {
        primary = annotation;
        map.remove(primary.annotationType());
    }

    Annotation removePrimary() {
        final Annotation p = primary;
        if (primary != null) {
            primary = null; // NOPMD
        }
        return p;
    }

    void remove(final Class<?> key) {
        final Annotation removed = map.remove(key);
        Verify.isFalse(removed == null, "Annotation map does not contain: %s", key.getName());
    }

    @VisibleForTesting
    Map<Class<?>, Annotation> getMap() {
        return map;
    }
}
