package org.instancio.creation.inheritance;

import org.instancio.pojo.inheritance.BaseClasSubClassInheritance;
import org.instancio.testsupport.tags.GenericsTag;
import org.instancio.testsupport.templates.CreationTestTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@GenericsTag
public class BaseClasSubClassInheritanceCreationTest extends CreationTestTemplate<BaseClasSubClassInheritance> {

    @Override
    protected void verify(BaseClasSubClassInheritance result) {
        final BaseClasSubClassInheritance.SubClass subClass = result.getSubClass();

        assertThat(subClass).isNotNull();
        assertThat(subClass.getSubClassField()).isInstanceOf(String.class);
        assertThat(subClass.getProtectedBaseClassField()).isInstanceOf(String.class);
        assertThat(subClass.getPrivateBaseClassField()).isInstanceOf(String.class);
    }
}
