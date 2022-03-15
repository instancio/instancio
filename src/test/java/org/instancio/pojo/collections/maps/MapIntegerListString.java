package org.instancio.pojo.collections.maps;

import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Getter
@ToString
public class MapIntegerListString {

    private Map<Integer, List<String>> map;
}
