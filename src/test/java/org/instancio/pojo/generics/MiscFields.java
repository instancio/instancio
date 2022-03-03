package org.instancio.pojo.generics;

import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.pojo.generics.container.Pair;
import org.instancio.pojo.generics.container.Triplet;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Bar;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Baz;
import org.instancio.pojo.generics.foobarbaz.itemcontainer.Foo;

import java.util.List;
import java.util.StringJoiner;

@Getter
public class MiscFields<A, B, C> {

    private A fieldA;
    private Pair<A, B> pairAB;
    private Pair<B, A> pairBA;
    private Pair<Long, Pair<Integer, String>> pairLongPairIntegerString;
    private Pair<A, Pair<Integer, String>> pairAPairIntegerString;
    private Pair<Foo<Bar<String>>, B> pairFooBarStringB;
    private Triplet<A, Foo<Bar<Baz<String>>>, List<C>> tripletA_FooBarBazString_ListOfC;
    private Pair<A, Foo<Bar<B>>> pairAFooBarB;
    private Pair<A, String> pairAString;
    private List<C> listOfCs;
    private List<Baz<String>> listOfBazStrings;
    private C[] arrayOfCs;
    private int[] arrayOfInts;
    private Foo<Bar<Pair<B, C>>> fooBarPairBC;
    private Foo<Bar<Baz<String>>> fooBarBazString;
    private List<Foo<List<Bar<List<Baz<List<String>>>>>>> listOfFoo_ListOfBar_ListOfBaz_ListOfString;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
