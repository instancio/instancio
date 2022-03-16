package org.instancio.pojo.collections.maps;

import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.ConcurrentNavigableMap;

@Getter
@ToString
public class ConcurrentNavigableMapIntegerString {

    private ConcurrentNavigableMap<Integer, String> map;
}
