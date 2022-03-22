package org.instancio.internal.reflection;

import org.instancio.internal.reflection.DeclaredFieldsCollector;
import org.instancio.internal.reflection.FieldCollector;
import org.instancio.pojo.person.Person;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DeclaredFieldsCollectorTest {

    private final FieldCollector fieldsCollector = new DeclaredFieldsCollector();

    @Test
    void getFields() {
        final List<Field> results = fieldsCollector.getFields(Person.class);
        assertThat(results)
                .extracting(Field::getName)
                .containsExactlyInAnyOrder(
                        "finalField",
                        "uuid",
                        "name",
                        "address",
                        "gender",
                        "age",
                        "lastModified",
                        "date",
                        "pets");
    }

}
