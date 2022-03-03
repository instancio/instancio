package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class GenericContainer<T> {
    private T value;
    private T[] array;
    private List<T> list;
}
