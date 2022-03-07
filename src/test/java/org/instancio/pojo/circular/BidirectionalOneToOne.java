package org.instancio.pojo.circular;

import lombok.Getter;
import lombok.ToString;

public class BidirectionalOneToOne {

    @Getter
    @ToString
    public static class Parent {
        private String parentName;
        private Child child;
    }

    @Getter
    @ToString
    public static class Child {
        private String childName;
        private Parent parent;
    }
}
