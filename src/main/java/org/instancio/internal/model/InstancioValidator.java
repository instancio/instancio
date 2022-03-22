package org.instancio.internal.model;

import org.instancio.exception.InstancioApiException;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class InstancioValidator {

    private InstancioValidator() {
        // non-instantiable
    }

    static void validateTypeParameters(Class<?> rootClass, List<Class<?>> rootTypeParameters) {
        final int typeVarsLength = rootClass.getTypeParameters().length;

        if (typeVarsLength == 0 && !rootTypeParameters.isEmpty()) {
            final String suppliedParams = rootTypeParameters.stream()
                    .map(Class::getSimpleName)
                    .collect(Collectors.joining(", "));

            throw new InstancioApiException(String.format(
                    "\nClass '%s' is not generic." +
                            "\nSpecifying type parameters 'withTypeParameters(%s)` is not valid for this class.",
                    rootClass.getName(), suppliedParams));
        }

        if (typeVarsLength != rootTypeParameters.size()) {
            throw new InstancioApiException(String.format(
                    "\nClass '%s' has %s type parameters: %s." +
                            "\nPlease specify the required type parameters using 'withTypeParameters(Class... types)`",
                    rootClass.getName(),
                    rootClass.getTypeParameters().length,
                    Arrays.toString(rootClass.getTypeParameters())));
        }
    }
}
