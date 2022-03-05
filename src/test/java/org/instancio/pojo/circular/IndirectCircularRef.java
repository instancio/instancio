package org.instancio.pojo.circular;

import lombok.Getter;
import lombok.ToString;

public class IndirectCircularRef {

    @Getter
    @ToString
    public static class A {
        private B b;
    }

    @Getter
    @ToString
    public static class B {
        private C c;
    }

    @Getter
    @ToString
    public static class C {
        private A a;
    }
}
