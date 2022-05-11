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
package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.instancio.testsupport.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Fail.fail;

@SuppressWarnings("UnusedReturnValue")
public class ReflectionAssert extends AbstractAssert<ReflectionAssert, Object> {
    private static final Logger LOG = LoggerFactory.getLogger(ReflectionAssert.class);

    private final SoftAssertions softly = new SoftAssertions();

    private ReflectionAssert(Object actual) {
        super(actual, ReflectionAssert.class);
    }

    public static ReflectionAssert assertThatObject(Object actual) {
        return new ReflectionAssert(actual);
    }

    public ReflectionAssert isFullyPopulated() {
        as("Object contains a null value: %s", actual).isNotNull();

        if (Collection.class.isAssignableFrom(actual.getClass())) {
            assertCollection((Collection<?>) actual, null);
            return this;
        } else if (Map.class.isAssignableFrom(actual.getClass())) {
            assertMap((Map<?, ?>) actual, null);
            return this;
        } else if (actual.getClass().isArray()) {
            assertArray(actual, null);
            return this;
        }

        if (actual.getClass().getPackage() == null || actual.getClass().getPackage().getName().startsWith("java")) {
            return this;
        }

        softly.assertThat(actual).hasNoNullFieldsOrProperties();

        LOG.trace("ASSERT OBJ " + actual.getClass());

        final List<Method> methods = Arrays.stream(actual.getClass().getDeclaredMethods())
                .filter(it -> it.getName().startsWith("get") && it.getParameterCount() == 0)
                .filter(it -> Modifier.isPublic(it.getModifiers()) && !Modifier.isStatic(it.getModifiers()))
                .collect(toList());

        for (Method method : methods) {

            try {
                LOG.trace("calling method: " + method);

                Object result = method.invoke(actual);
                softly.assertThat(result)
                        .as("Method '%s' returned a null", method)
                        .isNotNull();

                // False positive: 'result != null' is not always true due to soft assertions
                // noinspection ConstantConditions
                if (result != null) {
                    if (Collection.class.isAssignableFrom(result.getClass())) {
                        assertCollection((Collection<?>) result, method);
                    } else if (Map.class.isAssignableFrom(result.getClass())) {
                        assertMap((Map<?, ?>) result, method);
                    } else if (result.getClass().isArray()) {
                        assertArray(result, method);
                    } else {
                        assertThatObject(result).isFullyPopulated(); // recurse
                    }
                }

            } catch (IllegalAccessException | InvocationTargetException ex) {
                fail("Invocation of method '%s' failed with: '%s'", method.getName(), ex.getMessage());
            }
        }

        softly.assertAll();
        return this;
    }

    private void assertMap(Map<?, ?> map, @Nullable Method method) {
        softly.assertThat(map)
                .as("Method '%s' return unexpected result", method)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            assertThatObject(entry.getKey()).isFullyPopulated();
            assertThatObject(entry.getValue()).isFullyPopulated();
        }
    }

    private void assertCollection(Collection<?> collection, @Nullable Method method) {
        softly.assertThat(collection)
                .as("Method '%s' return unexpected result", method)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .allSatisfy(it -> assertThatObject(it).isFullyPopulated());
    }

    private void assertArray(Object array, @Nullable Method method) {
        final int size = Array.getLength(array);

        softly.assertThat(size)
                .as("Method '%s' return unexpected result", method)
                .isBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (int i = 0; i < size; i++) {
            Object element = Array.get(array, i);
            assertThatObject(element).isFullyPopulated();
        }
    }

}