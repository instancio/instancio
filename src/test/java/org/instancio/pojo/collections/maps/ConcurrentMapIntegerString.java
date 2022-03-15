package org.instancio.pojo.collections.maps;

import lombok.Getter;
import lombok.ToString;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentMap;

@Getter
@ToString
public class ConcurrentMapIntegerString {

    private ConcurrentMap<Integer, String> map;
}
