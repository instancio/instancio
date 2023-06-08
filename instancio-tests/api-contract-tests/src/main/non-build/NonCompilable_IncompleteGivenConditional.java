import org.instancio.*;

class NonCompilable_IncompleteGivenConditional {

    void nonCompilable() {
        // using String.class to avoid adding imports.
        // Specified class doesn't matter since the snippet shouldn't even compile
        Instancio.of(String.class)
                .when(When.given(Select.allStrings(), Select.allStrings()));
    }
}