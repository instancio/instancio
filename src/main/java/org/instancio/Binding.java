package org.instancio;

import javax.annotation.Nullable;

public class Binding {
    private enum BindingType {TYPE, FIELD}

    private final BindingType bindingType;
    private final Class<?> targetType;
    private final String fieldName;

    private Binding(final BindingType bindingType,
                    @Nullable final Class<?> targetType,
                    @Nullable final String fieldName) {

        this.bindingType = bindingType;
        this.targetType = targetType;
        this.fieldName = fieldName;
    }

    public static Binding fieldBinding(@Nullable final Class<?> targetType, final String fieldName) {
        return new Binding(BindingType.FIELD, targetType, fieldName);
    }

    public static Binding fieldBinding(final String fieldName) {
        return new Binding(BindingType.FIELD, null, fieldName);
    }

    public static Binding typeBinding(final Class<?> targetType) {
        return new Binding(BindingType.TYPE, targetType, null);
    }

    public boolean isFieldBinding() {
        return bindingType == BindingType.FIELD;
    }

    public Class<?> getTargetType() {
        return targetType;
    }

    public String getFieldName() {
        return fieldName;
    }
}
