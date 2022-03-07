package org.instancio.pojo.inheritance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

public class Inheritance {

    @Getter
    @Setter
    @ToString
    public static class BaseClass {
        private String privateBaseField;
        protected String protectedBaseField;
    }

    @Getter
    @Setter
    @ToString
    public static class ChildClass extends BaseClass {
        private String childField;
    }

}
