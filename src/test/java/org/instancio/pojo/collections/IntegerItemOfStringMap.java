package org.instancio.pojo.collections;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.generics.basic.Item;

import java.util.Map;

@Getter
@ToString
public class IntegerItemOfStringMap {
    private Map<Integer, Item<String>> mapField;
}
