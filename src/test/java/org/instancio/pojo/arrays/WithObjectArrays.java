package org.instancio.pojo.arrays;

import lombok.Getter;

public class WithObjectArrays {

    @Getter
    public static class PojoWithStringArray {
        private String[] values;
    }

    @Getter
    public static class PojoWithEnumArray {
        public enum NumberEnum {
            ONE, TWO, THREE
        }

        private NumberEnum[] values;
    }

    @Getter
    public static class PojoWithPojoArray {
        private PojoItem[] values;

        @Getter
        public static class PojoItem {
            private int id;
            private String name;
        }
    }

}
