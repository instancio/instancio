package org.instancio.pojo.generics;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class ListExtendsNumber {

    private List<? extends Number> list;
}
