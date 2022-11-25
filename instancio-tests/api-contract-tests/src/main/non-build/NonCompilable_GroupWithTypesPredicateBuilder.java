import org.instancio.*;

class NonCompilable_GroupWithTypesPredicateBuilder {

    void nonCompilable() {
        // Predicate builder for types is not groupable
        Select.all(Select.types());
    }
}