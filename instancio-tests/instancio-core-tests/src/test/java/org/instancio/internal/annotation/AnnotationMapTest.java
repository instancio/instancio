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
package org.instancio.internal.annotation;

import org.instancio.internal.util.ReflectionUtils;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AnnotationMapTest {

    @Retention(RUNTIME)
    private @interface Foo {}

    @Retention(RUNTIME)
    private @interface Bar {}

    @Retention(RUNTIME)
    private @interface Baz {}

    @Foo
    @Bar
    @Baz
    @SuppressWarnings("unused") // used via reflection
    private static final Object DUMMY = null;

    private static final Annotation[] ANNOTATIONS =
            ReflectionUtils.getField(AnnotationMapTest.class, "DUMMY").getDeclaredAnnotations();

    private static Annotation getAnnotation(Class<?> type) {
        return Arrays.stream(ANNOTATIONS)
                .filter(a -> a.annotationType() == type)
                .findAny().orElse(null);
    }

    /**
     * Annotations should be processed in the order they are declared.
     * Using a hash map may affect reproducibility.
     */
    @Test
    void shouldBeBackedByLinkedHashMap() {
        final AnnotationMap annotationMap = new AnnotationMap(ANNOTATIONS);
        assertThat(annotationMap.getMap())
                .as("Annotation map should preserve the declared order")
                .isExactlyInstanceOf(LinkedHashMap.class);
    }

    @Nested
    class NonEmptyMapTest {

        private final AnnotationMap map = new AnnotationMap(ANNOTATIONS);

        @Test
        void get() {
            final Foo foo = map.get(Foo.class);
            assertThat(foo).isNotNull();
        }

        @Test
        void remove() {
            map.remove(Foo.class);
            map.remove(Bar.class);

            final Foo foo = map.get(Foo.class);
            final Bar bar = map.get(Bar.class);
            final Baz baz = map.get(Baz.class);

            assertThat(foo).isNull();
            assertThat(bar).isNull();
            assertThat(baz).isNotNull();
        }

        @Test
        void getRemainingExceptPrimary() {
            final Annotation bar = getAnnotation(Bar.class);
            map.setPrimary(bar);

            assertThat(map.getAnnotations())
                    .containsExactly(getAnnotation(Foo.class), getAnnotation(Baz.class));
        }

        @Test
        void getRemovePrimary() {
            final Annotation bar = getAnnotation(Bar.class);
            map.setPrimary(bar);

            final Annotation removed = map.removePrimary();

            assertThat(removed).isEqualTo(bar);
            assertThat(map.getAnnotations())
                    .containsExactly(getAnnotation(Foo.class), getAnnotation(Baz.class));

            assertThat(map.removePrimary()).isNull();
        }

        @Test
        void getValues() {
            assertThat(map.getAnnotations()).containsExactly(ANNOTATIONS);
        }
    }

    @Nested
    class EmptyMapTest {
        private final AnnotationMap emptyMap = new AnnotationMap();

        @Test
        void get() {
            final Foo foo = emptyMap.get(Foo.class);
            assertThat(foo).isNull();
        }

        @Test
        void remove() {
            assertThatThrownBy(() -> emptyMap.remove(Foo.class))
                    .isExactlyInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Annotation map does not contain: %s", Foo.class.getName());
        }

        @Test
        void getRemainingExceptPrimary() {
            assertThat(emptyMap.getAnnotations()).isEmpty();
        }

        @Test
        void getRemovePrimary() {
            assertThat(emptyMap.removePrimary()).isNull();
        }

        @Test
        void getValues() {
            assertThat(emptyMap.getAnnotations()).isEmpty();
        }
    }
}
