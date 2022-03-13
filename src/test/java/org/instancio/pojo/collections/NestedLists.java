package org.instancio.pojo.collections;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class NestedLists {

    private List<List<String>> nested;
}
