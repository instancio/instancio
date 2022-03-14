package org.instancio.pojo.generics;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.pojo.generics.basic.Triplet;
import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;

import java.util.List;

@Getter
public class TripletAFooBarBazStringListOfB<A, B> {

    private Triplet<A, Foo<Bar<Baz<String>>>, List<B>> tripletA_FooBarBazString_ListOfB;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
