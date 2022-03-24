package org.instancio.pojo.basic;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IntegerHolderWithPrivateDefaultConstructor {

    private IntegerHolderWithPrivateDefaultConstructor() {
        // private for testing
    }

    private int primitive;

    private Integer wrapper;
}
