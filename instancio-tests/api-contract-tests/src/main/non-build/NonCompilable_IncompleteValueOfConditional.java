import org.instancio.*;

class NonCompilable_IncompleteValueOfConditional {

    void nonCompilable() {
        // using String.class to avoid adding imports.
        // Specified class doesn't matter since the snippet shouldn't even compile
        Instancio.of(String.class)
                .when(When.valueOf(Select.all(String.class)).satisfies(c -> true));
    }
}