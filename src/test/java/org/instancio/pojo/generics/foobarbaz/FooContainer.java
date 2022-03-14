package org.instancio.pojo.generics.foobarbaz;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FooContainer {

    private Foo<String> stringFoo;
    private Foo<Integer> integerFoo;

}
