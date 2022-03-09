package org.instancio.pojo.arrays.object;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WithPojoArray {
    private PojoItem[] values;

    @Getter
    @ToString
    @EqualsAndHashCode
    public static class PojoItem {
        private int id;
        private String name;
    }
}
