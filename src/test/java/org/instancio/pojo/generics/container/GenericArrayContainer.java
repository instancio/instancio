package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
@Setter
public class GenericArrayContainer<X, Y> {

    private GenericItem<X>[] itemArrayX;

    private GenericItem<Y>[] itemArrayY;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
