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
package org.instancio.junit.internal;

import org.instancio.test.support.pojo.basic.StringHolder;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReflectionUtilsTest {

    @Test
    void getAnnotatedFields() {
        final List<Field> fields = ReflectionUtils.getAnnotatedFields(WithAnnotatedField.class, AnnotationX.class);

        assertThat(fields).hasSize(2)
                .extracting(Field::getName)
                .contains("foo", "bar");
    }

    @Test
    void getAnnotatedFieldsReturnsEmptyList() {
        assertThat(ReflectionUtils.getAnnotatedFields(StringHolder.class, AnnotationX.class)).isEmpty();
    }


    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationX {}

    @Retention(RetentionPolicy.RUNTIME)
    private @interface AnnotationY {}

    @SuppressWarnings("all")
    private static class WithAnnotatedField {
        @AnnotationX
        private String foo;
        @AnnotationX
        private Integer bar;
        @AnnotationY
        private Integer baz;
    }
}
