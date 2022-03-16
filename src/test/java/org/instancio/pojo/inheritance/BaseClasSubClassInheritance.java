package org.instancio.pojo.inheritance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BaseClasSubClassInheritance {

    private SubClass subClass;

    @Getter
    @Setter
    @ToString
    public static class BaseClass {
        private String privateBaseClassField;
        protected String protectedBaseClassField;
    }

    @Getter
    @Setter
    @ToString
    public static class SubClass extends BaseClass {
        private String subClassField;
    }

}
