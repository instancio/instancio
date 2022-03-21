package org.instancio.pojo.arrays;

import lombok.Getter;
import lombok.ToString;
import org.instancio.pojo.generics.basic.Item;

@Getter
@ToString
public class TwoArraysOfItemString {

    private Item<String>[] array1;

    private Item<String>[] array2;
}
