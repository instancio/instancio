package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.pojo.generics.basic.Item;

@Getter
@Setter
public class ItemArrayContainer<X, Y> {

    private Item<X>[] itemArrayX;

    private Item<Y>[] itemArrayY;

    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
