package org.instancio.pojo.generics;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class NestedMaps<OKEY, IKEY> {

    private Map<Long, Map<String, Boolean>> map1;

    private Map<OKEY, Map<IKEY, Boolean>> map2;
}
