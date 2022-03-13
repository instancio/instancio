package org.instancio.pojo.generics.foobarbaz;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FooBarBazContainer {

    private Foo<Bar<Baz<String>>> item;

}
