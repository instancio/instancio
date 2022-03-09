package org.instancio.pojo.arrays;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WithPojoArray {
    private PojoItem[] values;

    @Getter
    public static class PojoItem {
        private int id;
        private String name;
    }
}
