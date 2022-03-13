package org.instancio.pojo.generics.foobarbaz;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.pojo.generics.basic.Pair;

@Getter
public class PairAFooBarB<A, B> {

    private Pair<A, Foo<Bar<B>>> pairAFooBarB;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
