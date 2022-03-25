package org.instancio.generators.coretypes;

import org.instancio.GeneratorSpec;

public interface StringGeneratorSpec extends GeneratorSpec<String> {

    StringGeneratorSpec prefix(String prefix);

    StringGeneratorSpec nullable();

    StringGeneratorSpec allowEmpty();

    StringGeneratorSpec minLength(int length);

    StringGeneratorSpec maxLength(int length);
}
