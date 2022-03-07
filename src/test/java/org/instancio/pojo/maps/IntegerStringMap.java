package org.instancio.pojo.maps;

import lombok.Getter;
import lombok.ToString;

import java.util.Map;

@Getter
@ToString
public class IntegerStringMap {
    private Map<Integer, String> mapField;
}
