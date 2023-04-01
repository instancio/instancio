package org.instancio.test.support.pojo.misc.getters;

// used via reflection
@SuppressWarnings("unused")
public class BeanStylePojo {

    private String foo;
    private boolean bar;

    public String getFoo() {
        return foo;
    }

    public boolean isBar() {
        return bar;
    }
}
