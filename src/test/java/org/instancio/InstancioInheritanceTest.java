package org.instancio;

import org.junit.jupiter.api.Test;
import org.instancio.pojo.inheritance.Inheritance;

import static org.assertj.core.api.Assertions.assertThat;

public class InstancioInheritanceTest {

    @Test
    void inheritanceChildClass() {
        Inheritance.ChildClass result = Instancio.of(Inheritance.ChildClass.class).create();

        System.out.println(result);

        assertThat(result.getChildField()).isNotBlank();
        assertThat(result.getProtectedBaseField()).isNotBlank();
        assertThat(result.getPrivateBaseField()).isNotBlank();
    }

}
