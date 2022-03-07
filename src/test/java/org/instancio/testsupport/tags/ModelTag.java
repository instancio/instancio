package org.instancio.testsupport.tags;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Tag("model")
@Retention(RetentionPolicy.RUNTIME)
public @interface ModelTag {
}
