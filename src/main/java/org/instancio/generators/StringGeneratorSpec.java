package org.instancio.generators;

import org.instancio.GeneratorSpec;

public interface StringGeneratorSpec extends GeneratorSpec<String> {

    StringGeneratorSpec prefix(String prefix);

    StringGeneratorSpec allowEmpty(); // XXX

    StringGeneratorSpec min(int length);

    StringGeneratorSpec max(int length);
}
