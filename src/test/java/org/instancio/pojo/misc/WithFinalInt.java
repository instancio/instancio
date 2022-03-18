package org.instancio.pojo.misc;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WithFinalInt {

    // TODO is it possible to instantiate the class without assigning the final field
    private final int value;

    public WithFinalInt(int value) {
        this.value = value;
    }

}
