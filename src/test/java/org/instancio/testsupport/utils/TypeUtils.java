package org.instancio.testsupport.utils;

import java.lang.reflect.TypeVariable;

public class TypeUtils {

    public static TypeVariable<?> getTypeVar(Class<?> klass, String typeParameter) {
        for (TypeVariable<?> tvar : klass.getTypeParameters()) {
            if (tvar.getName().equals(typeParameter)) {
                return tvar;
            }
        }
        throw new IllegalArgumentException(String.format("Invalid type parameter '%s' for %s", typeParameter, klass));
    }


}
