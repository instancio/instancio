package org.instancio.test.contract.condition;

import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

public class UnconditionalModuleExportCondition extends ArchCondition<JavaClass> {
    public UnconditionalModuleExportCondition() {
        super("be unconditionally exported");
    }

    @Override
    public void check(final JavaClass javaClass, final ConditionEvents events) {
        final Class<?> actualClass = javaClass.reflect();
        final Module module = actualClass.getModule();

        if (module.isExported(actualClass.getPackageName())) {
            events.add(SimpleConditionEvent.satisfied(javaClass, "%s is unconditionally exported".formatted(javaClass.getName())));
        } else {
            events.add(SimpleConditionEvent.violated(javaClass, "%s is not unconditionally exported".formatted(javaClass.getName())));
        }
    }
}
