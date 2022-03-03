package org.instancio.pojo.generics.foobarbaz.itemcontainer;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Foo<X> {
    private X fooValue;
    private Object otherFooValue;
}
