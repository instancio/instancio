package org.instancio.pojo.cyclic;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class IndirectCircularRef {

    private A startA;

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
        private A endA;
    }
}
