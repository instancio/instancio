package org.instancio.internal.model;

import org.instancio.exception.InstancioApiException;
import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

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

    static void validateSubtypeMapping(final Class<?> from, final Class<?> to) {
        Verify.notNull(from, "'from' class must not be null");
        Verify.notNull(to, "'to' class must not be null");
        if (from == to) {
            throw new InstancioApiException(String.format("Cannot map the class to itself: '%s'", to.getName()));
        }
        if (!from.isAssignableFrom(to)) {
            throw new InstancioApiException(String.format(
                    "Class '%s' is not a subtype of '%s'", to.getName(), from.getName()));
        }
        if (!ReflectionUtils.isConcrete(to)) {
            throw new InstancioApiException(String.format(
                    "'to' class must not be an interface or abstract class: '%s'", to.getName()));
        }
    }
}
