package org.instancio.pojo.basic;

import lombok.Getter;

@Getter
public class ClassWithInitializedField {
    public static final String DEFAULT_FIELD_VALUE = "some value";

    @SuppressWarnings("FieldMayBeFinal")
    private String value = DEFAULT_FIELD_VALUE;
}
