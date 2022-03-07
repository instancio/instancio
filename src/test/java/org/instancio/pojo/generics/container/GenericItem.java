package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GenericItem<K> {
    private K value;
}
