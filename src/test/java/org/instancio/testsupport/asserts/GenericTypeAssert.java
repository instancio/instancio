package org.instancio.testsupport.asserts;

import org.assertj.core.api.AbstractAssert;
import org.instancio.model.GenericType;

import java.lang.reflect.Type;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("UnusedReturnValue")
public class GenericTypeAssert extends AbstractAssert<GenericTypeAssert, GenericType> {

    private GenericTypeAssert(GenericType actual) {
        super(actual, GenericTypeAssert.class);
    }

    public static GenericTypeAssert assertGenericType(GenericType actual) {
        return new GenericTypeAssert(actual);
    }

    public GenericTypeAssert hasRawType(Class<?> expected) {
        isNotNull();
        assertThat(actual.getRawType()).isEqualTo(expected);
        return this;
    }

    public GenericTypeAssert hasGenericType(Type expected) {
        isNotNull();
        assertThat(actual.getGenericType()).isEqualTo(expected);
        return this;
    }

}