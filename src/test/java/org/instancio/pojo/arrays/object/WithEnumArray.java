package org.instancio.pojo.arrays.object;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WithEnumArray {

    private NumberEnum[] values;

    public enum NumberEnum {
        ONE, TWO, THREE
    }
}
