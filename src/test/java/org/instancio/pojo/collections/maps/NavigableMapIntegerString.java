package org.instancio.pojo.collections.maps;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;
import java.util.NavigableMap;
import java.util.SortedMap;

@Getter
@ToString
public class NavigableMapIntegerString {

    private NavigableMap<Integer, String> map;
}
