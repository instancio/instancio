import org.instancio.*;

class NonCompilable_GroupWithPredicateTypeSelector {

    void nonCompilable() {
        // Predicate type selector is not groupable
        Select.all(Select.types(p -> true));
    }
}