package org.instancio.internal.reflection.instantiation;

import org.instancio.exception.InstancioException;
import org.instancio.pojo.basic.IntegerHolder;
import org.instancio.pojo.basic.IntegerHolderWithPrivateDefaultConstructor;
import org.instancio.pojo.basic.IntegerHolderWithoutDefaultConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InstantiatorTest {

    private final Instantiator instantiator = new Instantiator();

    @ValueSource(classes = {
            TreeSet.class,
            HashMap.class,
            IntegerHolder.class,
            IntegerHolderWithoutDefaultConstructor.class,
            IntegerHolderWithPrivateDefaultConstructor.class
    })
    @ParameterizedTest
    void instantiate(Class<?> klass) {
        assertThat(instantiator.instantiate(klass)).isNotNull();
    }

    @Test
    void instantiateError() {
        final Class<?> klass = List.class;
        assertThatThrownBy(() -> instantiator.instantiate(klass))
                .isInstanceOf(InstancioException.class)
                .hasMessage("Failed instantiating class '%s'", klass.getName());
    }
}
