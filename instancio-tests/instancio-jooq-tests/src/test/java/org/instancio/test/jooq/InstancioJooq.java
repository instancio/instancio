package org.instancio.test.jooq;

import org.instancio.Instancio;
import org.instancio.Model;
import org.instancio.settings.AssignmentType;
import org.instancio.settings.Keys;
import org.instancio.settings.MethodModifier;
import org.instancio.settings.OnSetMethodNotFound;
import org.instancio.settings.OnSetMethodUnmatched;
import org.instancio.settings.Settings;

public final class InstancioJooq {

    // Populate objects using public setters only
    private static final int SETTER_EXCLUSIONS = MethodModifier.PACKAGE_PRIVATE
            | MethodModifier.PROTECTED
            | MethodModifier.PRIVATE;

    private static final Settings SETTINGS = Settings.create()
            .set(Keys.ASSIGNMENT_TYPE, AssignmentType.METHOD)
            .set(Keys.SETTER_EXCLUDE_MODIFIER, SETTER_EXCLUSIONS)
            .set(Keys.ON_SET_METHOD_NOT_FOUND, OnSetMethodNotFound.IGNORE)
            .set(Keys.ON_SET_METHOD_UNMATCHED, OnSetMethodUnmatched.INVOKE)
            .lock();

    public static <T> Model<T> jooqModel(final Class<T> klass) {
        return Instancio.of(klass)
                .withSettings(SETTINGS)
                .toModel();
    }

}
