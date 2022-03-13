package org.instancio.pojo.generics;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Map;

@Getter
public class NestedMaps<OKEY, IKEY> {

    private Map<Long, Map<String, Boolean>> map1;

    private Map<OKEY, Map<IKEY, Boolean>> map2;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
