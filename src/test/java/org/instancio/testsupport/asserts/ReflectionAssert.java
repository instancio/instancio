package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.SoftAssertions;
import org.instancio.testsupport.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                        assertCollection(method, (Collection<?>) result);
                    } else if (Map.class.isAssignableFrom(result.getClass())) {
                        assertMap(method, (Map<?, ?>) result);
                    } else if (result.getClass().isArray()) {
                        assertArray(method, result);
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

    private void assertMap(Method method, Map<?, ?> map) {
        softly.assertThat(map)
                .as("Method '%s' return unexpected result", method)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE);

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            assertThatObject(entry.getKey()).isFullyPopulated();
            assertThatObject(entry.getValue()).isFullyPopulated();
        }
    }

    private void assertCollection(Method method, Collection<?> collection) {
        softly.assertThat(collection)
                .as("Method '%s' return unexpected result", method)
                .hasSizeBetween(Constants.MIN_SIZE, Constants.MAX_SIZE)
                .allSatisfy(it -> assertThatObject(it).isFullyPopulated());
    }

    private void assertArray(Method method, Object array) {
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