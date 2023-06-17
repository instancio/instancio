import org.instancio.*;

class NonCompilable_IncompleteGivenWithOneArg {

    void nonCompilable() {
        // using String.class to avoid adding imports.
        // Specified class doesn't matter since the snippet shouldn't even compile
        Instancio.of(String.class)
                .assign(Assign.given(Select.all(String.class)).satisfies(c -> true));
    }
}