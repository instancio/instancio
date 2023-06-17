import org.instancio.*;

class NonCompilable_IncompleteGivenWithTwoArgs {

    void nonCompilable() {
        // using String.class to avoid adding imports.
        // Specified class doesn't matter since the snippet shouldn't even compile
        Instancio.of(String.class)
                .assign(Assign.given(Select.allStrings(), Select.allStrings()));
    }
}