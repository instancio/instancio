package org.instancio.testsupport.templates;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation for tests using the {@link CreationTestTemplate}.
 * <p>
 * Specifies that auto-verification of created objects via reflection should be disabled.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoVerificationDisabled {
}
