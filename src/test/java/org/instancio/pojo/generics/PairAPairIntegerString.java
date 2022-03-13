package org.instancio.pojo.generics;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.pojo.generics.basic.Pair;

@Getter
public class PairAPairIntegerString<A> {

    private Pair<A, Pair<Integer, String>> pairAPairIntegerString;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
