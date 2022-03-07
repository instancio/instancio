package org.instancio.pojo.interfaces;

import lombok.Getter;

public class MultipleInterfaceImpls {

    public interface Widget {
        String getWidgetName();
    }

    @Getter
    public static class WidgetA implements Widget {
        private String widgetName;
    }

    @Getter
    public static class WidgetB implements Widget {
        private String widgetName;
    }

    @Getter
    public static class WidgetContainer {
        private Widget widget;
    }
}
