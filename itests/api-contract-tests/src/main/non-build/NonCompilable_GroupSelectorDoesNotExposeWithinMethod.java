import org.instancio.*;

class NonCompilable_GroupSelectorDoesNotExposeWithinMethod {

    void nonCompilable() {
        // Group selector does not have a 'within()' method
        Select.all(Select.all(String.class)).within(Select.scope(String.class));
    }
}