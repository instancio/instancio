package org.instancio.pojo.generics.outermidinner;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

@Getter
public class ListOfOuterMidInnerString {

    private List<Outer<Mid<Inner<String>>>> rootList = new ArrayList<>();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
