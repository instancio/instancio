package org.instancio.pojo.collections.lists;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.generics.basic.Item;

import java.util.List;

@Getter
@ToString
public class TwoListsWithItemString {

    private List<Item<String>> list1;

    private List<Item<String>> list2;
}
