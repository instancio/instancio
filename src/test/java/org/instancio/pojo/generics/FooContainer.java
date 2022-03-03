package org.instancio.pojo.generics;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FooContainer {

    private Foo<String> item;
    private Foo<Integer> numItem;

    @Getter
    @Setter
    @ToString
    public static class Foo<T> {
        private T fooValue;
        private Object otherFooValue;
    }
}
