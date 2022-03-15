package org.instancio.pojo.collections.lists;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ListListString {

    private List<List<String>> nested;
}
