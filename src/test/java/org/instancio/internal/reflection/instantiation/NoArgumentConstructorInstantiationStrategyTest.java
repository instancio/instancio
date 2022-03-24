package org.instancio.internal.reflection.instantiation;

import org.instancio.pojo.basic.IntegerHolder;
import org.instancio.pojo.basic.IntegerHolderWithPrivateDefaultConstructor;
import org.instancio.pojo.basic.IntegerHolderWithoutDefaultConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NoArgumentConstructorInstantiationStrategyTest {

    private final InstantiationStrategy strategy = new NoArgumentConstructorInstantiationStrategy();

    @ValueSource(classes = {
            ArrayList.class,
            TreeSet.class,
            IntegerHolder.class,
            IntegerHolderWithPrivateDefaultConstructor.class
    })
    @ParameterizedTest
    void createInstanceSuccessful(Class<?> klass) {
        assertThat(strategy.createInstance(klass)).isNotNull();
    }

    @Test
    void createInstanceFails() {
        final Class<?> klass = IntegerHolderWithoutDefaultConstructor.class;
        assertThatThrownBy(() -> strategy.createInstance(klass))
                .isInstanceOf(InstantiationStrategyException.class)
                .hasMessage("Error instantiating %s", klass);
    }
}
