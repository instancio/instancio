import org.instancio.*;

class NonCompilable_IncompleteConditional {

    void nonCompilable() {
        // using String.class to avoid adding imports.
        // Specified class doesn't matter since the snippet shouldn't even compile
        Instancio.of(String.class)
                .when(Select.valueOf(Select.all(String.class)).satisfies(c -> true));
    }
}