package org.instancio.pojo.basic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public class IntegerHolderWithoutDefaultConstructor {

    private int primitive;

    private Integer wrapper;
}
