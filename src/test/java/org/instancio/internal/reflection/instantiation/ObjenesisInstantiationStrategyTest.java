package org.instancio.internal.reflection.instantiation;

import org.instancio.pojo.basic.IntegerHolderWithoutDefaultConstructor;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObjenesisInstantiationStrategyTest {

    private final InstantiationStrategy strategy = new ObjenesisInstantiationStrategy();

    @Test
    void createInstanceSuccessful() {
        assertThat(strategy.createInstance(IntegerHolderWithoutDefaultConstructor.class)).isNotNull();
    }

}
