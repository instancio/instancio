package org.instancio.pojo.circular;

import lombok.Getter;

public class IndirectCircularRef {

    @Getter
    public static class A {
        private B b;
    }

    @Getter
    public static class B {
        private C c;
    }

    @Getter
    public static class C {
        private A a;
    }
}
