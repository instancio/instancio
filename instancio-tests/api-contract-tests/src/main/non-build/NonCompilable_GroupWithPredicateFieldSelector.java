import org.instancio.*;

class NonCompilable_GroupWithPredicateFieldSelector {

    void nonCompilable() {
        // Predicate field selector is not groupable
        Select.all(Select.fields(p -> true));
    }
}