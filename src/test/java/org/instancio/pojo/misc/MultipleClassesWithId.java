package org.instancio.pojo.misc;

import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

//
//        A
//      / |  \
//    ID  B    C
//        |   /|\
//       ID  B D ID
//           |  \
//           ID  ID
@Getter
public class MultipleClassesWithId {

    private A a;

    @Getter
    @ToString
    public static class ID {
        private UUID id;
    }

    @Getter
    @ToString
    public static class A {
        private ID id;
        private B b;
        private C c;
    }

    @Getter
    @ToString
    public static class B {
        private ID id;
    }

    @Getter
    @ToString
    public static class C {
        private ID id;
        private B b;
        private D d;
    }

    @Getter
    @ToString
    public static class D {
        private ID id;
    }
}