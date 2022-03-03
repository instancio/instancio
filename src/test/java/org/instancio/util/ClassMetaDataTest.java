package org.instancio.util;

import experimental.reflection.PClass;
import org.junit.jupiter.api.Test;
import org.instancio.pojo.generics.FooContainer;

import static org.assertj.core.api.Assertions.assertThat;

class ClassMetaDataTest {


    @Test
    void from() {
        final PClass pClass = PClass.from(FooContainer.class);
        //final PClass pClass = PClass.from(Person.class);

        System.out.println(pClass);
    }
}
