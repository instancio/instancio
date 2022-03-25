package org.instancio.testsupport.tags;

import org.junit.jupiter.api.Tag;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Tag for {@link org.instancio.settings.Settings} tests.
 */
@Tag("settings")
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface SettingsTag {
}
