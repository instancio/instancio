package org.instancio.test.beanvalidation.adhoc;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.instancio.test.support.tags.Feature;
import org.instancio.test.support.tags.FeatureTag;
import org.instancio.test.support.util.Constants;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * See: https://github.com/instancio/instancio/issues/1324
 */
@FeatureTag(Feature.BEAN_VALIDATION)
@ExtendWith(InstancioExtension.class)
class GH1324Test {

    @Retention(RetentionPolicy.RUNTIME)
    private @interface Foo {}

    @Data
    private static final class Pojo {
        @Foo
        private String nullableField;

        @Foo
        @NotNull
        private String notNullableField;
    }

    @Test
    void shouldNotThrowNpeWhenUsingAdHocAnnotations() {
        final Settings settings = Settings.create()
                .set(Keys.BEAN_VALIDATION_ENABLED, true)
                .set(Keys.STRING_NULLABLE, true)
                // disable nullable collections and elements
                // set by this module's instancio.properties
                .set(Keys.COLLECTION_NULLABLE, false)
                .set(Keys.COLLECTION_ELEMENTS_NULLABLE, false);

        final List<Pojo> results = Instancio.ofList(Pojo.class)
                .size(Constants.SAMPLE_SIZE_DDD)
                .withSettings(settings)
                .withSetting(Keys.FAIL_ON_ERROR, true)
                .create();

        assertThat(results).isNotNull();
        assertThat(results).extracting(Pojo::getNullableField).containsNull();
        assertThat(results).extracting(Pojo::getNotNullableField).doesNotContainNull();
    }
}
