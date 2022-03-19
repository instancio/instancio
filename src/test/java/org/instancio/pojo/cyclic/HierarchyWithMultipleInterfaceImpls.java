package org.instancio.pojo.cyclic;

import lombok.Getter;

public class HierarchyWithMultipleInterfaceImpls {

    interface Identifiable {
        ID getId();
    }

    @Getter
    public static class A implements Identifiable {
        ID id;
        B b;
        C c;
    }

    @Getter
    public static class B implements Identifiable {
        ID id;
    }

    @Getter
    public static class C implements Identifiable {
        ID id;
    }

    @Getter
    public static class ID {
        private String id;
        private Identifiable owner;
    }
}
