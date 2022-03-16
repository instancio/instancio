package org.instancio.pojo.collections.maps;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.generics.basic.Item;

import java.util.Map;

@Getter
@ToString
public class MapIntegerItemOfString {

    private Map<Integer, Item<String>> map;
}
