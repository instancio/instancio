package org.instancio.pojo.interfaces;

import lombok.Getter;
import lombok.ToString;

public class SingleInterfaceImpl {
    public interface Widget {
        String getWidgetName();
    }

    @ToString
    @Getter
    public static class WidgetImpl implements Widget {
        private String widgetName;
    }

    @ToString
    @Getter
    public static class WidgetContainer {
        private Widget widget;
    }
}
