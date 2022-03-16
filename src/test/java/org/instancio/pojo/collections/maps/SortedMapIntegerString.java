package org.instancio.pojo.collections.maps;

import lombok.Getter;
import lombok.ToString;

import java.util.SortedMap;

@Getter
@ToString
public class SortedMapIntegerString {

    private SortedMap<Integer, String> map;
}
